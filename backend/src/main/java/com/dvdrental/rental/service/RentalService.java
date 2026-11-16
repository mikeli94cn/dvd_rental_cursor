package com.dvdrental.rental.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.common.security.SecurityUtils;
import com.dvdrental.customer.entity.Customer;
import com.dvdrental.customer.repository.CustomerRepository;
import com.dvdrental.inventory.entity.DvdCopy;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.repository.DvdCopyRepository;
import com.dvdrental.inventory.service.DvdCopyStatusMachine;
import com.dvdrental.pricing.PricingService;
import com.dvdrental.rental.dto.CreateRentalRequest;
import com.dvdrental.rental.dto.RentalItemResponse;
import com.dvdrental.rental.dto.RentalOrderResponse;
import com.dvdrental.rental.dto.ReturnItemRequest;
import com.dvdrental.rental.entity.RentalItem;
import com.dvdrental.rental.entity.RentalItemStatus;
import com.dvdrental.rental.entity.RentalOrder;
import com.dvdrental.rental.entity.RentalOrderStatus;
import com.dvdrental.rental.repository.RentalItemRepository;
import com.dvdrental.rental.repository.RentalOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RentalService {

    private final RentalOrderRepository rentalOrderRepository;
    private final RentalItemRepository rentalItemRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final CustomerRepository customerRepository;
    private final PricingService pricingService;
    private final DvdCopyStatusMachine dvdCopyStatusMachine;
    private final RentalOrderStatusMachine rentalOrderStatusMachine;

    public RentalService(RentalOrderRepository rentalOrderRepository,
                         RentalItemRepository rentalItemRepository,
                         DvdCopyRepository dvdCopyRepository,
                         CustomerRepository customerRepository,
                         PricingService pricingService,
                         DvdCopyStatusMachine dvdCopyStatusMachine,
                         RentalOrderStatusMachine rentalOrderStatusMachine) {
        this.rentalOrderRepository = rentalOrderRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.customerRepository = customerRepository;
        this.pricingService = pricingService;
        this.dvdCopyStatusMachine = dvdCopyStatusMachine;
        this.rentalOrderStatusMachine = rentalOrderStatusMachine;
    }

    @Transactional
    public RentalOrderResponse createRental(CreateRentalRequest request) {
        Customer customer = requireCurrentCustomer();
        Set<Long> uniqueIds = new HashSet<>(request.dvdCopyIds());
        if (uniqueIds.size() != request.dvdCopyIds().size()) {
            throw ApiException.badRequest("Duplicate DVD copy ids in request");
        }

        List<DvdCopy> copies = dvdCopyRepository.findAllByIdForUpdate(uniqueIds);
        if (copies.size() != uniqueIds.size()) {
            throw ApiException.notFound("One or more DVD copies were not found");
        }

        RentalOrder order = new RentalOrder();
        order.setCustomer(customer);
        order.setStatus(RentalOrderStatus.ACTIVE);
        BigDecimal total = BigDecimal.ZERO;
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        for (DvdCopy copy : copies) {
            if (copy.getStatus() != DvdCopyStatus.AVAILABLE) {
                throw ApiException.conflict("DVD copy " + copy.getBarcode() + " is not available");
            }
            dvdCopyStatusMachine.assertTransition(copy.getStatus(), DvdCopyStatus.RENTED);
            copy.setStatus(DvdCopyStatus.RENTED);

            int rentalDays = request.rentalDays() != null
                    ? request.rentalDays()
                    : copy.getMovie().getRentalDaysDefault();
            BigDecimal lineAmount = pricingService.calculateRentalCharge(copy.getMovie().getDailyRate(), rentalDays);

            RentalItem item = new RentalItem();
            item.setDvdCopy(copy);
            item.setDueDate(today.plusDays(rentalDays));
            item.setLineAmount(lineAmount);
            item.setOverdueAmount(BigDecimal.ZERO);
            item.setStatus(RentalItemStatus.RENTED);
            order.addItem(item);
            total = total.add(lineAmount);
        }

        order.setTotalAmount(total);
        RentalOrder saved = rentalOrderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<RentalOrderResponse> myRentals(Pageable pageable) {
        Customer customer = requireCurrentCustomer();
        return rentalOrderRepository.findByCustomerId(customer.getId(), pageable).map(this::toResponseSummary);
    }

    @Transactional(readOnly = true)
    public RentalOrderResponse getRental(Long id) {
        RentalOrder order = rentalOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> ApiException.notFound("Rental order not found"));
        assertCanView(order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<RentalOrderResponse> searchStaff(Long customerId, RentalOrderStatus status, Pageable pageable) {
        return rentalOrderRepository.search(customerId, status, pageable).map(this::toResponseSummary);
    }

    @Transactional
    public RentalOrderResponse confirmReturn(ReturnItemRequest request) {
        if (!SecurityUtils.isStaffOrAdmin()) {
            throw ApiException.forbidden("Only staff can confirm returns");
        }
        RentalItem item = rentalItemRepository.findById(request.rentalItemId())
                .orElseThrow(() -> ApiException.notFound("Rental item not found"));
        if (item.getStatus() == RentalItemStatus.RETURNED) {
            throw ApiException.conflict("Rental item already returned");
        }
        if (item.getStatus() != RentalItemStatus.RENTED && item.getStatus() != RentalItemStatus.OVERDUE) {
            throw ApiException.conflict("Rental item cannot be returned from status " + item.getStatus());
        }

        DvdCopyStatus inspection = request.inspectionResult();
        if (inspection != DvdCopyStatus.AVAILABLE
                && inspection != DvdCopyStatus.DAMAGED
                && inspection != DvdCopyStatus.MAINTENANCE
                && inspection != DvdCopyStatus.LOST) {
            throw ApiException.badRequest("Inspection result must be AVAILABLE, DAMAGED, MAINTENANCE, or LOST");
        }

        DvdCopy copy = dvdCopyRepository.findByIdForUpdate(item.getDvdCopy().getId())
                .orElseThrow(() -> ApiException.notFound("DVD copy not found"));

        LocalDate returnDate = LocalDate.now(ZoneOffset.UTC);
        BigDecimal overdue = pricingService.calculateOverdueCharge(
                copy.getMovie().getDailyRate(), item.getDueDate(), returnDate);
        item.setOverdueAmount(overdue);
        item.setReturnedAt(Instant.now());
        item.setStatus(RentalItemStatus.RETURNED);

        dvdCopyStatusMachine.assertTransition(copy.getStatus(), DvdCopyStatus.RETURNED_PENDING_INSPECTION);
        copy.setStatus(DvdCopyStatus.RETURNED_PENDING_INSPECTION);
        dvdCopyStatusMachine.assertTransition(copy.getStatus(), inspection);
        copy.setStatus(inspection);
        if (request.notes() != null) {
            copy.setNotes(request.notes());
        }

        RentalOrder order = item.getRentalOrder();
        order.setTotalAmount(order.getTotalAmount().add(overdue));
        boolean allReturned = order.getItems().stream().allMatch(i -> i.getStatus() == RentalItemStatus.RETURNED);
        if (allReturned && order.getStatus() == RentalOrderStatus.ACTIVE) {
            rentalOrderStatusMachine.assertTransition(order.getStatus(), RentalOrderStatus.COMPLETED);
            order.setStatus(RentalOrderStatus.COMPLETED);
        }

        return toResponse(rentalOrderRepository.findByIdWithItems(order.getId()).orElse(order));
    }

    private Customer requireCurrentCustomer() {
        return customerRepository.findByUserEmailIgnoreCase(SecurityUtils.currentEmail())
                .orElseThrow(() -> ApiException.forbidden("Customer profile required"));
    }

    private void assertCanView(RentalOrder order) {
        if (SecurityUtils.isStaffOrAdmin()) {
            return;
        }
        Customer customer = requireCurrentCustomer();
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("Cannot view another customer's rental");
        }
    }

    private RentalOrderResponse toResponseSummary(RentalOrder order) {
        return toResponse(order);
    }

    private RentalOrderResponse toResponse(RentalOrder order) {
        Customer customer = order.getCustomer();
        List<RentalItemResponse> items = order.getItems().stream().map(this::toItemResponse).toList();
        return new RentalOrderResponse(
                order.getId(),
                customer.getId(),
                customer.getUser().getEmail(),
                customer.getFirstName() + " " + customer.getLastName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }

    private RentalItemResponse toItemResponse(RentalItem item) {
        DvdCopy copy = item.getDvdCopy();
        RentalItemStatus status = item.getStatus();
        if (status == RentalItemStatus.RENTED && item.getDueDate().isBefore(LocalDate.now(ZoneOffset.UTC))) {
            status = RentalItemStatus.OVERDUE;
        }
        return new RentalItemResponse(
                item.getId(),
                copy.getId(),
                copy.getBarcode(),
                copy.getMovie().getId(),
                copy.getMovie().getTitle(),
                item.getDueDate(),
                item.getReturnedAt(),
                item.getLineAmount(),
                item.getOverdueAmount(),
                status
        );
    }
}
