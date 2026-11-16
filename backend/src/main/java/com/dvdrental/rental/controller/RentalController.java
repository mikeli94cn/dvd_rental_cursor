package com.dvdrental.rental.controller;

import com.dvdrental.rental.dto.CreateRentalRequest;
import com.dvdrental.rental.dto.RentalOrderResponse;
import com.dvdrental.rental.dto.ReturnItemRequest;
import com.dvdrental.rental.entity.RentalOrderStatus;
import com.dvdrental.rental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rentals")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public RentalOrderResponse create(@Valid @RequestBody CreateRentalRequest request) {
        return rentalService.createRental(request);
    }

    @GetMapping("/rentals/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<RentalOrderResponse> myRentals(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return rentalService.myRentals(pageable);
    }

    @GetMapping("/rentals/{id}")
    public RentalOrderResponse get(@PathVariable Long id) {
        return rentalService.getRental(id);
    }

    @GetMapping("/staff/rentals")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Page<RentalOrderResponse> staffSearch(@RequestParam(required = false) Long customerId,
                                                 @RequestParam(required = false) RentalOrderStatus status,
                                                 @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                 Pageable pageable) {
        return rentalService.searchStaff(customerId, status, pageable);
    }

    @PostMapping("/staff/returns")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public RentalOrderResponse confirmReturn(@Valid @RequestBody ReturnItemRequest request) {
        return rentalService.confirmReturn(request);
    }
}
