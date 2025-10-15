package org.example.bankcards.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "pan_encrypted", columnDefinition = "text")
    private String panEncrypted;

    @Column(name = "pan_suffix", length = 4)
    private String panSuffix;

    @Column(name = "holder_name")
    private String holderName;

    @Column(name = "expiry_month")
    private Integer expiryMonth;

    @Column(name = "expiry_year")
    private Integer expiryYear;

    @Column(name = "status")
    private String status;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Card() { this.id = UUID.randomUUID(); }
}