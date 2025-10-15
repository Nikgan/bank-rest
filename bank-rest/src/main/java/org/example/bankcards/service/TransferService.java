package org.example.bankcards.service;

import lombok.RequiredArgsConstructor;
import org.example.bankcards.entity.Card;
import org.example.bankcards.entity.Transfer;
import org.example.bankcards.exception.ApiException;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.repository.TransferRepository;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new ApiException("Исходная карта не найдена", HttpStatus.NOT_FOUND.value()));
        Card to = cardRepository.findWithLockingById(toCardId)
                .orElseThrow(() -> new ApiException("Целевая карта не найдена", HttpStatus.NOT_FOUND.value()));

        if (!"ACTIVE".equals(from.getStatus()) || !"ACTIVE".equals(to.getStatus())) {
            throw new ApiException("Обе карты должны быть активными", HttpStatus.BAD_REQUEST.value());
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new ApiException("Недостаточно средств на исходной карте", HttpStatus.BAD_REQUEST.value());
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
