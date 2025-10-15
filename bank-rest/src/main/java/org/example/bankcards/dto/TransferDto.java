package org.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private UUID id;
    private UUID fromCardId;
    private UUID toCardId;
    private BigDecimal amount;
    private String status;
    private OffsetDateTime createdAt;
    private String description;
}
