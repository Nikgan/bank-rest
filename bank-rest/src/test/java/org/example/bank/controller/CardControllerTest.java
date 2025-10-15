package org.example.bank.controller;

import org.example.bank.dto.CardDto;
import org.example.bank.mapper.CardMapper;
import org.example.bank.model.Card;
import org.example.bank.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMyCards_returnsPage() {
        UUID uid = UUID.randomUUID();
        Card c = new Card(); c.setId(UUID.randomUUID()); c.setPanSuffix("1234");
        Page<CardDto> p = new PageImpl<>(List.of(CardMapper.toDto(c)));

        when(cardService.getUserCards(eq(uid), any(), any(), any(), any(Pageable.class))).thenReturn(p);

        ResponseEntity<?> resp = cardController.getMyCards(uid, null, null, null, 0, 10);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void createCard_callsService() {
        UUID uid = UUID.randomUUID();
        Card c = new Card();
        when(cardService.createCard(eq(uid), anyString())).thenReturn(c);

        ResponseEntity<Card> resp = cardController.createCard(uid, "Ivan");
        assertEquals(200, resp.getStatusCodeValue());
        verify(cardService).createCard(uid, "Ivan");
    }
}
