package org.example.bank.repository;

import org.example.bank.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByOwnerId(UUID ownerId);
    Optional<Card> findByIdAndOwnerId(UUID id, UUID ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> findWithLockingById(UUID id);

    Page<Card> findByOwnerId(UUID ownerId, Pageable pageable);

    // Фильтрация с optional параметрами
    @Query("SELECT c FROM Card c " +
            "WHERE c.ownerId = :ownerId " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:holderName IS NULL OR LOWER(c.holderName) LIKE LOWER(CONCAT('%', :holderName, '%'))) " +
            "AND (:panSuffix IS NULL OR c.panSuffix = :panSuffix)")
    Page<Card> findFilteredByOwner(
            @Param("ownerId") UUID ownerId,
            @Param("status") String status,
            @Param("holderName") String holderName,
            @Param("panSuffix") String panSuffix,
            Pageable pageable);
}