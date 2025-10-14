package org.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.example.bank.model.Card;
import org.example.bank.model.Transfer;
import org.example.bank.repository.CardRepository;
import org.example.bank.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    @Transactional
    public Transfer makeTransfer(UUID fromCardId, UUID toCardId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма перевода должна быть больше 0");
        }

        Card from = cardRepository.findWithLockingById(fromCardId)
                .orElseThrow(() -> new RuntimeException("Исходная карта не найдена"));
        Card to = cardRepository.findWithLockingById(toCardId)
                .orElseThrow(() -> new RuntimeException("Целевая карта не найдена"));

        if (!"ACTIVE".equals(from.getStatus()) || !"ACTIVE".equals(to.getStatus())) {
            throw new RuntimeException("Обе карты должны быть активными");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств на исходной карте");
        }

        // Списание и зачисление
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepository.save(from);
        cardRepository.save(to);

        // Сохраняем историю перевода
        Transfer transfer = new Transfer();
        transfer.setFromCardId(fromCardId);
        transfer.setToCardId(toCardId);
        transfer.setAmount(amount);
        transfer.setStatus("COMPLETED");
        transfer.setCreatedAt(OffsetDateTime.now());
        transfer.setDescription(description != null ? description : "");
        return transferRepository.save(transfer);
    }

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public List<Transfer> getTransfersByCard(UUID cardId) {
        return transferRepository.findAll().stream()
                .filter(t -> t.getFromCardId().equals(cardId) || t.getToCardId().equals(cardId))
                .toList();
    }
}
