package com.dvdrental.inventory.controller;

import com.dvdrental.inventory.dto.DvdCopyRequest;
import com.dvdrental.inventory.dto.DvdCopyResponse;
import com.dvdrental.inventory.dto.DvdCopyStatusUpdateRequest;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/movies/{movieId}/availability")
    public Page<DvdCopyResponse> availability(@PathVariable Long movieId,
                                              @PageableDefault(size = 50) Pageable pageable) {
        return inventoryService.search(movieId, DvdCopyStatus.AVAILABLE, null, pageable);
    }

    @GetMapping("/staff/inventory")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Page<DvdCopyResponse> search(@RequestParam(required = false) Long movieId,
                                        @RequestParam(required = false) DvdCopyStatus status,
                                        @RequestParam(required = false) String barcode,
                                        @PageableDefault(size = 20) Pageable pageable) {
        return inventoryService.search(movieId, status, barcode, pageable);
    }

    @GetMapping("/staff/inventory/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public DvdCopyResponse get(@PathVariable Long id) {
        return inventoryService.get(id);
    }

    @PostMapping("/staff/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public DvdCopyResponse create(@Valid @RequestBody DvdCopyRequest request) {
        return inventoryService.create(request);
    }

    @PutMapping("/staff/inventory/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public DvdCopyResponse updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody DvdCopyStatusUpdateRequest request) {
        return inventoryService.updateStatus(id, request);
    }
}
