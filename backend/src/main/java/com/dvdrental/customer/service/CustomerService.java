package com.dvdrental.customer.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.common.security.SecurityUtils;
import com.dvdrental.customer.dto.CustomerResponse;
import com.dvdrental.customer.dto.UpdateProfileRequest;
import com.dvdrental.customer.entity.Customer;
import com.dvdrental.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public CustomerResponse myProfile() {
        Customer customer = customerRepository.findByUserEmailIgnoreCase(SecurityUtils.currentEmail())
                .orElseThrow(() -> ApiException.notFound("Customer profile not found"));
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateProfile(UpdateProfileRequest request) {
        Customer customer = customerRepository.findByUserEmailIgnoreCase(SecurityUtils.currentEmail())
                .orElseThrow(() -> ApiException.notFound("Customer profile not found"));
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setPhone(request.phone());
        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> listCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        return toResponse(customerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Customer not found")));
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getUser().getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone()
        );
    }
}
