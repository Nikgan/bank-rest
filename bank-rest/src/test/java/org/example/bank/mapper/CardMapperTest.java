package org.example.bank.mapper;

import org.example.bank.dto.CardDto;
import org.example.bank.model.Card;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CardMapperTest {

    @Test
    void toDto_masksPanSuffix() {
        Card c = new Card();
        c.setHolderName("Ivan");
        c.setPanSuffix("1234");
        c.setExpiryMonth(12);
        c.setExpiryYear(2030);
        c.setStatus("ACTIVE");
        c.setBalance(new BigDecimal("100.00"));

        CardDto dto = CardMapper.toDto(c);
        assertNotNull(dto);
        assertEquals("**** **** **** 1234", dto.getMaskedPan());
        assertEquals("Ivan", dto.getHolderName());
    }

    @Test
    void toDto_handlesNullSuffix() {
        Card c = new Card();
        c.setPanSuffix(null);
        CardDto dto = CardMapper.toDto(c);
        assertEquals("**** **** **** 0000", dto.getMaskedPan());
    }
}
