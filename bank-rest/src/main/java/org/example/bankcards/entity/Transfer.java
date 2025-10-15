package org.example.bankcards.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    private UUID id;

    @Column(name = "from_card_id")
    private UUID fromCardId;

    @Column(name = "to_card_id")
    private UUID toCardId;

    private BigDecimal amount;

    private String status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    private String description;

    public Transfer() { this.id = UUID.randomUUID(); }

}