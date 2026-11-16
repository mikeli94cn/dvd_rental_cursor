package com.dvdrental.inventory.dto;

import com.dvdrental.inventory.entity.DvdCopyStatus;

public record DvdCopyResponse(
        Long id,
        Long movieId,
        String movieTitle,
        String barcode,
        DvdCopyStatus status,
        String notes
) {
}
