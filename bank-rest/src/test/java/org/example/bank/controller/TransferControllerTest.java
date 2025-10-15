package org.example.bank.controller;

import org.example.bank.dto.TransferDto;
import org.example.bank.model.Card;
import org.example.bank.model.Transfer;
import org.example.bank.service.CardService;
import org.example.bank.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferControllerTest {

    @Mock
    private TransferService transferService;
    @Mock
    private CardService cardService;
    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void makeTransfer_ownerCheck_and_callService() {
        UUID userId = UUID.randomUUID();
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();

        Card fromCard = new Card(); fromCard.setId(from);
        when(cardService.getUserCard(userId, from)).thenReturn(java.util.Optional.of(fromCard));
        Transfer tr = new Transfer();
        when(transferService.makeTransfer(from, to, new BigDecimal("10.00"), null)).thenReturn(tr);

        ResponseEntity<?> resp = transferController.makeTransfer(userId, from, to, new BigDecimal("10.00"), null);

        assertEquals(200, resp.getStatusCodeValue());
        verify(transferService).makeTransfer(from, to, new BigDecimal("10.00"), null);
    }

    @Test
    void getMyTransfers_filtersProperly() {
        UUID userId = UUID.randomUUID();
        Card c1 = new Card(); c1.setId(UUID.randomUUID());
        when(cardService.getUserCards(userId)).thenReturn(List.of(c1));

        Transfer t1 = new Transfer(); t1.setFromCardId(c1.getId());
        Transfer t2 = new Transfer(); t2.setFromCardId(UUID.randomUUID());

        when(transferService.getAllTransfers()).thenReturn(List.of(t1, t2));

        ResponseEntity<List<TransferDto>> resp = transferController.getMyTransfers(userId);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
    }
}
