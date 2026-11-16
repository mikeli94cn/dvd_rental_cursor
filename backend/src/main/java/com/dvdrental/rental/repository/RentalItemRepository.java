package com.dvdrental.rental.repository;

import com.dvdrental.rental.entity.RentalItem;
import com.dvdrental.rental.entity.RentalItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {

    Optional<RentalItem> findByIdAndStatusIn(Long id, List<RentalItemStatus> statuses);

    @Query("""
            SELECT i FROM RentalItem i
            JOIN FETCH i.dvdCopy c
            JOIN FETCH c.movie
            JOIN FETCH i.rentalOrder o
            JOIN FETCH o.customer cust
            JOIN FETCH cust.user
            WHERE i.status IN :statuses
              AND i.dueDate < :today
            """)
    Page<RentalItem> findOverdue(@Param("statuses") List<RentalItemStatus> statuses,
                                 @Param("today") LocalDate today,
                                 Pageable pageable);

    long countByStatusInAndDueDateBefore(List<RentalItemStatus> statuses, LocalDate date);
}
