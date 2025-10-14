package org.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.bank.dto.CardDto;
import org.example.bank.mapper.CardMapper;
import org.example.bank.model.Card;
import org.example.bank.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Получение всех карт пользователя
    @GetMapping
    public ResponseEntity<List<CardDto>> getMyCards(@AuthenticationPrincipal(expression = "id") UUID userId) {
        List<CardDto> cards = cardService.getUserCards(userId).stream()
                .map(CardMapper::toDto)
                .toList();
        return ResponseEntity.ok(cards);
    }

    // Получение всех карт (только для ADMIN)
    @GetMapping("/all")
    public ResponseEntity<List<CardDto>> getAllCards() {
        List<CardDto> cards = cardService.getAllCards().stream()
                .map(CardMapper::toDto)
                .toList();
        return ResponseEntity.ok(cards);
    }

    // Создание новой карты
    @PostMapping
    public ResponseEntity<Card> createCard(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam String holderName
    ) {
        return ResponseEntity.ok(cardService.createCard(userId, holderName));
    }

    // Получение баланса
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<?> getBalance(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID cardId
    ) {
        Card card = cardService.getUserCard(userId, cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена или не принадлежит пользователю"));
        return ResponseEntity.ok(card.getBalance());
    }

    // Блокировка карты
    @PostMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID cardId
    ) {
        Card card = cardService.getUserCard(userId, cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена или не принадлежит пользователю"));
        cardService.blockCard(card.getId());
        return ResponseEntity.ok("Карта заблокирована");
    }

    // Удаление карты
    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID cardId
    ) {
        Card card = cardService.getUserCard(userId, cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена или не принадлежит пользователю"));
        cardService.deleteCard(card.getId());
        return ResponseEntity.ok("Карта удалена");
    }
}
