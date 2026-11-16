package com.dvdrental.reporting.service;

import com.dvdrental.catalog.repository.MovieRepository;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.repository.DvdCopyRepository;
import com.dvdrental.pricing.PricingService;
import com.dvdrental.rental.entity.RentalItemStatus;
import com.dvdrental.rental.entity.RentalOrderStatus;
import com.dvdrental.rental.repository.RentalItemRepository;
import com.dvdrental.rental.repository.RentalOrderRepository;
import com.dvdrental.reporting.dto.DashboardStatsResponse;
import com.dvdrental.reporting.dto.OverdueItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ReportingService {

    private final MovieRepository movieRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final RentalOrderRepository rentalOrderRepository;
    private final RentalItemRepository rentalItemRepository;
    private final PricingService pricingService;

    public ReportingService(MovieRepository movieRepository,
                            DvdCopyRepository dvdCopyRepository,
                            RentalOrderRepository rentalOrderRepository,
                            RentalItemRepository rentalItemRepository,
                            PricingService pricingService) {
        this.movieRepository = movieRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.rentalOrderRepository = rentalOrderRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.pricingService = pricingService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse dashboard() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        long overdue = rentalItemRepository.countByStatusInAndDueDateBefore(
                List.of(RentalItemStatus.RENTED, RentalItemStatus.OVERDUE), today);
        return new DashboardStatsResponse(
                movieRepository.count(),
                dvdCopyRepository.count(),
                dvdCopyRepository.countByStatus(DvdCopyStatus.AVAILABLE),
                rentalOrderRepository.findByStatus(RentalOrderStatus.ACTIVE, Pageable.unpaged()).getTotalElements(),
                overdue
        );
    }

    @Transactional(readOnly = true)
    public Page<OverdueItemResponse> overdueReport(Pageable pageable) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return rentalItemRepository.findOverdue(
                List.of(RentalItemStatus.RENTED, RentalItemStatus.OVERDUE), today, pageable
        ).map(item -> {
            long days = pricingService.overdueDays(item.getDueDate(), today);
            BigDecimal charge = pricingService.calculateOverdueCharge(
                    item.getDvdCopy().getMovie().getDailyRate(), item.getDueDate(), today);
            return new OverdueItemResponse(
                    item.getId(),
                    item.getRentalOrder().getId(),
                    item.getRentalOrder().getCustomer().getUser().getEmail(),
                    item.getRentalOrder().getCustomer().getFirstName() + " "
                            + item.getRentalOrder().getCustomer().getLastName(),
                    item.getDvdCopy().getMovie().getTitle(),
                    item.getDvdCopy().getBarcode(),
                    item.getDueDate(),
                    days,
                    charge
            );
        });
    }
}
