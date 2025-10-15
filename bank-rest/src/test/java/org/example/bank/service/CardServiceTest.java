package org.example.bank.service;

import org.example.bank.dto.CardDto;
import org.example.bank.mapper.CardMapper;
import org.example.bank.model.Card;
import org.example.bank.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CardService Unit Tests")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание карты должно сохранять карту с корректным PAN и статусом ACTIVE")
    void testCreateCard() {
        UUID ownerId = UUID.randomUUID();
        String holder = "John Doe";

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        Card result = cardService.createCard(ownerId, holder);

        verify(cardRepository).save(captor.capture());
        Card saved = captor.getValue();

        assertEquals(ownerId, saved.getOwnerId());
        assertEquals("ACTIVE", saved.getStatus());
        assertEquals(holder, saved.getHolderName());
        assertNotNull(saved.getPanEncrypted());
        assertEquals(saved.getPanSuffix().length(), 4);
        assertNotNull(result);
    }

    @Test
    @DisplayName("getUserCards должен вызывать repository.findByOwnerId()")
    void testGetUserCards() {
        UUID ownerId = UUID.randomUUID();
        List<Card> expected = List.of(new Card(), new Card());
        when(cardRepository.findByOwnerId(ownerId)).thenReturn(expected);

        List<Card> result = cardService.getUserCards(ownerId);
        assertEquals(expected, result);
        verify(cardRepository).findByOwnerId(ownerId);
    }

    @Test
    @DisplayName("getAllCards должен возвращать все карты")
    void testGetAllCards() {
        List<Card> expected = List.of(new Card());
        when(cardRepository.findAll()).thenReturn(expected);

        List<Card> result = cardService.getAllCards();
        assertEquals(expected, result);
        verify(cardRepository).findAll();
    }

    @Test
    @DisplayName("getCard должен возвращать Optional из findById()")
    void testGetCard() {
        UUID id = UUID.randomUUID();
        Card card = new Card();
        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.getCard(id);
        assertTrue(result.isPresent());
        assertEquals(card, result.get());
    }

    @Test
    @DisplayName("getUserCard должен искать по cardId и ownerId")
    void testGetUserCard() {
        UUID owner = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        when(cardRepository.findByIdAndOwnerId(cardId, owner)).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.getUserCard(owner, cardId);
        assertTrue(result.isPresent());
        verify(cardRepository).findByIdAndOwnerId(cardId, owner);
    }

    @Test
    @DisplayName("blockCard должен устанавливать статус BLOCKED и сохранять карту")
    void testBlockCard() {
        UUID id = UUID.randomUUID();
        Card card = new Card();
        card.setStatus("ACTIVE");

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.blockCard(id);

        assertEquals("BLOCKED", card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("blockCard должен выбрасывать исключение, если карта не найдена")
    void testBlockCard_NotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cardService.blockCard(id));
    }

    @Test
    @DisplayName("activateCard должен устанавливать статус ACTIVE и сохранять карту")
    void testActivateCard() {
        UUID id = UUID.randomUUID();
        Card card = new Card();
        card.setStatus("BLOCKED");

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.activateCard(id);

        assertEquals("ACTIVE", card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("activateCard должен выбрасывать исключение, если карта не найдена")
    void testActivateCard_NotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cardService.activateCard(id));
    }

    @Test
    @DisplayName("deleteCard должен вызывать deleteById в репозитории")
    void testDeleteCard() {
        UUID id = UUID.randomUUID();
        cardService.deleteCard(id);
        verify(cardRepository).deleteById(id);
    }

    @Test
    @DisplayName("getBalance должен возвращать баланс карты")
    void testGetBalance() {
        UUID id = UUID.randomUUID();
        Card card = new Card();
        card.setBalance(BigDecimal.TEN);

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBalance(id);
        assertEquals(BigDecimal.TEN, result);
    }

    @Test
    @DisplayName("getBalance должен бросать исключение, если карта не найдена")
    void testGetBalance_NotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cardService.getBalance(id));
    }

    @Test
    @DisplayName("encodePan и decodePan должны быть обратимыми")
    void testEncodeDecodePan() {
        String original = "1234567812345678";
        String encoded = cardService.decodePan(
                java.util.Base64.getEncoder().encodeToString(original.getBytes())
        );
        assertEquals(original, encoded);
    }

    @Test
    @DisplayName("getUserCards с фильтрацией должен вызывать findFilteredByOwner и маппить результат в DTO")
    void testGetUserCardsFiltered() {
        UUID ownerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Card card = new Card();
        card.setHolderName("John");
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findFilteredByOwner(ownerId, "ACTIVE", "John", "1234", pageable))
                .thenReturn(page);

        org.example.bank.dto.CardDto dto = new org.example.bank.dto.CardDto();
        dto.setHolderName("John");
        dto.setStatus("ACTIVE");
        dto.setMaskedPan("**** **** **** 1234");

        try (MockedStatic<CardMapper> mapperMock = mockStatic(CardMapper.class)) {
            mapperMock.when(() -> CardMapper.toDto(card)).thenReturn(dto);

            Page<org.example.bank.dto.CardDto> result = cardService.getUserCards(ownerId, "ACTIVE", "John", "1234", pageable);

            assertNotNull(result, "Page should not be null");
            assertEquals(1, result.getTotalElements());
            assertEquals("John", result.getContent().get(0).getHolderName());
            verify(cardRepository).findFilteredByOwner(ownerId, "ACTIVE", "John", "1234", pageable);
        }
    }
}
