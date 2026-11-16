package com.dvdrental.inventory.dto;

import com.dvdrental.inventory.entity.DvdCopyStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DvdCopyStatusUpdateRequest(
        @NotNull DvdCopyStatus status,
        @Size(max = 500) String notes
) {
}
