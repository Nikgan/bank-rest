package org.example.bankcards.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.CardDto;
import org.example.bankcards.mapper.CardMapper;
import org.example.bankcards.entity.Card;
import org.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<CardDto>> getMyCards(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String holderName,
            @RequestParam(required = false) String panLast4,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardDto> result = cardService.getUserCards(userId, status, holderName, panLast4, pageable);
        return ResponseEntity.ok(result);
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
