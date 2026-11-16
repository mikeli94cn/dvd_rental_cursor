package com.dvdrental.rental.dto;

import com.dvdrental.inventory.entity.DvdCopyStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReturnItemRequest(
        @NotNull Long rentalItemId,
        @NotNull DvdCopyStatus inspectionResult,
        @Size(max = 500) String notes
) {
}
