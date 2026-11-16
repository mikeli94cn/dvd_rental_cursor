package com.dvdrental.identity.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        List<String> roles,
        Long customerId,
        String firstName,
        String lastName,
        String phone
) {
}
