package org.example.bank.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class CardDto {
    private UUID id;
    private String maskedPan;
    private String holderName;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String status;
    private BigDecimal balance;
}