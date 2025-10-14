package org.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.bank.dto.UserDto;
import org.example.bank.mapper.UserMapper;
import org.example.bank.model.Card;
import org.example.bank.repository.CardRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    // Получить всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // Получить все карты
    @GetMapping("/cards")
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    // Заблокировать карту
    @PostMapping("/cards/{id}/block")
    public ResponseEntity<String> blockCard(@PathVariable UUID id) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setStatus("BLOCKED");
                    cardRepository.save(card);
                    return ResponseEntity.ok("Карта заблокирована");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Активировать карту
    @PostMapping("/cards/{id}/activate")
    public ResponseEntity<String> activateCard(@PathVariable UUID id) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setStatus("ACTIVE");
                    cardRepository.save(card);
                    return ResponseEntity.ok("Карта активирована");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Удалить карту
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable UUID id) {
        if (!cardRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cardRepository.deleteById(id);
        return ResponseEntity.ok("Карта удалена");
    }
}
