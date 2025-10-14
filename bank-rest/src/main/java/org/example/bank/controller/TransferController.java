package org.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.bank.dto.TransferDto;
import org.example.bank.mapper.TransferMapper;
import org.example.bank.model.Card;
import org.example.bank.model.Transfer;
import org.example.bank.service.CardService;
import org.example.bank.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final CardService cardService;

    // Перевод между картами(всеми)
    @PostMapping
    public ResponseEntity<TransferDto> makeTransfer(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam UUID fromCardId,
            @RequestParam UUID toCardId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        cardService.getUserCard(userId, fromCardId)
                .orElseThrow(() -> new RuntimeException("Вы не владелец карты-отправителя"));

        Transfer transfer = transferService.makeTransfer(fromCardId, toCardId, amount, description);
        return ResponseEntity.ok(TransferMapper.toDto(transfer));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransferDto>> getMyTransfers(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        List<UUID> myCardIds = cardService.getUserCards(userId).stream()
                .map(c -> c.getId())
                .toList();

        List<TransferDto> mine = transferService.getAllTransfers().stream()
                .filter(t -> myCardIds.contains(t.getFromCardId()) || myCardIds.contains(t.getToCardId()))
                .map(TransferMapper::toDto)
                .toList();

        return ResponseEntity.ok(mine);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransferDto>> getAllTransfers() {
        List<TransferDto> all = transferService.getAllTransfers().stream()
                .map(TransferMapper::toDto)
                .toList();
        return ResponseEntity.ok(all);
    }
}