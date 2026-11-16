package com.dvdrental.inventory.repository;

import com.dvdrental.inventory.entity.DvdCopy;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DvdCopyRepository extends JpaRepository<DvdCopy, Long> {

    Optional<DvdCopy> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    Page<DvdCopy> findByMovieId(Long movieId, Pageable pageable);

    Page<DvdCopy> findByStatus(DvdCopyStatus status, Pageable pageable);

    long countByMovieIdAndStatus(Long movieId, DvdCopyStatus status);

    long countByStatus(DvdCopyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM DvdCopy c WHERE c.id IN :ids")
    List<DvdCopy> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM DvdCopy c WHERE c.id = :id")
    Optional<DvdCopy> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT c FROM DvdCopy c
            WHERE (:movieId IS NULL OR c.movie.id = :movieId)
              AND (:status IS NULL OR c.status = :status)
              AND (:barcode IS NULL OR LOWER(c.barcode) LIKE LOWER(CONCAT('%', :barcode, '%')))
            """)
    Page<DvdCopy> search(@Param("movieId") Long movieId,
                         @Param("status") DvdCopyStatus status,
                         @Param("barcode") String barcode,
                         Pageable pageable);
}
