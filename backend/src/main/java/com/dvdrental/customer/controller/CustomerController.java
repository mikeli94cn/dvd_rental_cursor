package com.dvdrental.customer.controller;

import com.dvdrental.customer.dto.CustomerResponse;
import com.dvdrental.customer.dto.UpdateProfileRequest;
import com.dvdrental.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public CustomerResponse me() {
        return customerService.myProfile();
    }

    @PutMapping("/customers/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public CustomerResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return customerService.updateProfile(request);
    }

    @GetMapping("/staff/customers")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Page<CustomerResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return customerService.listCustomers(pageable);
    }

    @GetMapping("/staff/customers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public CustomerResponse get(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }
}
