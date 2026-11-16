package com.dvdrental.customer.dto;

public record CustomerResponse(
        Long id,
        Long userId,
        String email,
        String firstName,
        String lastName,
        String phone
) {
}
