package org.example.bank.mapper;

import org.example.bank.dto.CardDto;
import org.example.bank.model.Card;

public class CardMapper {
    public static CardDto toDto(Card c) {
        if (c == null) return null;
        CardDto d = new CardDto();
        d.setId(c.getId());
        d.setHolderName(c.getHolderName());
        d.setExpiryMonth(c.getExpiryMonth());
        d.setExpiryYear(c.getExpiryYear());
        d.setStatus(c.getStatus());
        d.setBalance(c.getBalance());
        String suffix = c.getPanSuffix() != null ? c.getPanSuffix() : "0000";
        d.setMaskedPan("**** **** **** " + suffix);
        return d;
    }
}