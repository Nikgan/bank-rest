package org.example.bank.service;

import org.example.bank.model.Card;
import org.example.bank.model.Transfer;
import org.example.bank.repository.CardRepository;
import org.example.bank.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    private UUID fromId;
    private UUID toId;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fromId = UUID.randomUUID();
        toId = UUID.randomUUID();

        fromCard = new Card();
        fromCard.setId(fromId);
        fromCard.setStatus("ACTIVE");
        fromCard.setBalance(new BigDecimal("500.00"));

        toCard = new Card();
        toCard.setId(toId);
        toCard.setStatus("ACTIVE");
        toCard.setBalance(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Успешный перевод между активными картами")
    void testMakeTransferSuccess() {
        when(cardRepository.findWithLockingById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findWithLockingById(toId)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        Transfer result = transferService.makeTransfer(fromId, toId, new BigDecimal("50.00"), "Test transfer");

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(new BigDecimal("450.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("150.00"), toCard.getBalance());
        verify(cardRepository, times(2)).findWithLockingById(any());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    @DisplayName("Ошибка при недостаточном балансе")
    void testMakeTransferNotEnoughFunds() {
        fromCard.setBalance(new BigDecimal("10"));
        when(cardRepository.findWithLockingById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findWithLockingById(toId)).thenReturn(Optional.of(toCard));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                transferService.makeTransfer(fromId, toId, new BigDecimal("100.00"), "Too much")
        );
        assertTrue(ex.getMessage().contains("Недостаточно средств"));
        verify(cardRepository, times(2)).findWithLockingById(any());
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при неактивной карте")
    void testMakeTransferInactiveCard() {
        fromCard.setStatus("BLOCKED");
        when(cardRepository.findWithLockingById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findWithLockingById(toId)).thenReturn(Optional.of(toCard));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                transferService.makeTransfer(fromId, toId, new BigDecimal("10.00"), null)
        );
        assertTrue(ex.getMessage().contains("активными"));
    }

    @Test
    @DisplayName("Эмуляция конкурентного доступа с pessimistic lock")
    void testConcurrentAccessLocking() {
        when(cardRepository.findWithLockingById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findWithLockingById(toId)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Едлаем два потока, вызывающих один и тот же метод
        Runnable transferTask = () ->
                transferService.makeTransfer(fromId, toId, new BigDecimal("10.00"), "Concurrent");

        transferTask.run();
        transferTask.run();

        // Чек, что findWithLockingById вызывался последовательно для обеих карт
        InOrder order = inOrder(cardRepository);
        order.verify(cardRepository, times(1)).findWithLockingById(fromId);
        order.verify(cardRepository, times(1)).findWithLockingById(toId);
        // при втором вызове тож самое
        order.verify(cardRepository, times(1)).findWithLockingById(fromId);
        order.verify(cardRepository, times(1)).findWithLockingById(toId);

        verify(cardRepository, atLeast(4)).findWithLockingById(any());
        verify(cardRepository, atLeast(2)).save(any(Card.class));
        verify(transferRepository, atLeast(2)).save(any(Transfer.class));
    }
}
