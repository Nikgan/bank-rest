package org.example.bankcards.mapper;

import org.example.bankcards.dto.TransferDto;
import org.example.bankcards.entity.Transfer;

public class TransferMapper {

    public static TransferDto toDto(Transfer t) {
        if (t == null) return null;

        TransferDto dto = new TransferDto();
        dto.setId(t.getId());
        dto.setFromCardId(t.getFromCardId());
        dto.setToCardId(t.getToCardId());
        dto.setAmount(t.getAmount());
        dto.setStatus(t.getStatus());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setDescription(t.getDescription());

        return dto;
    }
}
