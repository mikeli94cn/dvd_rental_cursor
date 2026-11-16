package com.dvdrental.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DvdCopyRequest(
        @NotNull Long movieId,
        @NotBlank @Size(max = 64) String barcode,
        @Size(max = 500) String notes
) {
}
