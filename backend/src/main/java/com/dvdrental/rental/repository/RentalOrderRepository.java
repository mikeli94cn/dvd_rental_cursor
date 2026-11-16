package com.dvdrental.rental.repository;

import com.dvdrental.rental.entity.RentalOrder;
import com.dvdrental.rental.entity.RentalOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> {

    Page<RentalOrder> findByCustomerId(Long customerId, Pageable pageable);

    Page<RentalOrder> findByStatus(RentalOrderStatus status, Pageable pageable);

    @Query("""
            SELECT DISTINCT o FROM RentalOrder o
            LEFT JOIN FETCH o.items i
            LEFT JOIN FETCH i.dvdCopy c
            LEFT JOIN FETCH c.movie
            WHERE o.id = :id
            """)
    Optional<RentalOrder> findByIdWithItems(@Param("id") Long id);

    @Query("""
            SELECT o FROM RentalOrder o
            WHERE (:customerId IS NULL OR o.customer.id = :customerId)
              AND (:status IS NULL OR o.status = :status)
            """)
    Page<RentalOrder> search(@Param("customerId") Long customerId,
                             @Param("status") RentalOrderStatus status,
                             Pageable pageable);
}
