package org.example.bankcards.mapper;

import org.example.bankcards.dto.CardDto;
import org.example.bankcards.entity.Card;

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