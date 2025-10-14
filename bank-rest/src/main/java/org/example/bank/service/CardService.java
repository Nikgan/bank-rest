package org.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.example.bank.model.Card;
import org.example.bank.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final SecureRandom random = new SecureRandom();

    // CRUD карты
    public Card createCard(UUID ownerId, String holderName) {
        Card card = new Card();
        card.setOwnerId(ownerId);
        card.setHolderName(holderName);
        card.setStatus("ACTIVE");
        String pan = generatePan();
        card.setPanEncrypted(encodePan(pan));
        card.setPanSuffix(pan.substring(12));
        return cardRepository.save(card);
    }

    public List<Card> getUserCards(UUID ownerId) {
        return cardRepository.findByOwnerId(ownerId);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Optional<Card> getCard(UUID cardId) {
        return cardRepository.findById(cardId);
    }

    public Optional<Card> getUserCard(UUID ownerId, UUID cardId) {
        return cardRepository.findByIdAndOwnerId(cardId, ownerId);
    }

    public void blockCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("BLOCKED");
        cardRepository.save(card);
    }

    public void activateCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("ACTIVE");
        cardRepository.save(card);
    }

    public void deleteCard(UUID cardId) {
        cardRepository.deleteById(cardId);
    }

    public BigDecimal getBalance(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        return card.getBalance();
    }

    // Всё про карту и её шифр/дешифр

    private String generatePan() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String encodePan(String pan) {
        return java.util.Base64.getEncoder().encodeToString(pan.getBytes());
    }

    public String decodePan(String encodedPan) {
        return new String(java.util.Base64.getDecoder().decode(encodedPan));
    }
}
