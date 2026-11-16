package com.dvdrental.inventory.service;

import com.dvdrental.catalog.entity.Movie;
import com.dvdrental.catalog.repository.MovieRepository;
import com.dvdrental.common.error.ApiException;
import com.dvdrental.inventory.dto.DvdCopyRequest;
import com.dvdrental.inventory.dto.DvdCopyResponse;
import com.dvdrental.inventory.dto.DvdCopyStatusUpdateRequest;
import com.dvdrental.inventory.entity.DvdCopy;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.repository.DvdCopyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final DvdCopyRepository dvdCopyRepository;
    private final MovieRepository movieRepository;
    private final DvdCopyStatusMachine statusMachine;

    public InventoryService(DvdCopyRepository dvdCopyRepository,
                            MovieRepository movieRepository,
                            DvdCopyStatusMachine statusMachine) {
        this.dvdCopyRepository = dvdCopyRepository;
        this.movieRepository = movieRepository;
        this.statusMachine = statusMachine;
    }

    @Transactional(readOnly = true)
    public Page<DvdCopyResponse> search(Long movieId, DvdCopyStatus status, String barcode, Pageable pageable) {
        String barcodeQuery = (barcode == null || barcode.isBlank()) ? null : barcode.trim();
        return dvdCopyRepository.search(movieId, status, barcodeQuery, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DvdCopyResponse get(Long id) {
        return toResponse(dvdCopyRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("DVD copy not found")));
    }

    @Transactional
    public DvdCopyResponse create(DvdCopyRequest request) {
        if (dvdCopyRepository.existsByBarcode(request.barcode())) {
            throw ApiException.conflict("Barcode already exists");
        }
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> ApiException.notFound("Movie not found"));
        DvdCopy copy = new DvdCopy();
        copy.setMovie(movie);
        copy.setBarcode(request.barcode().trim());
        copy.setStatus(DvdCopyStatus.AVAILABLE);
        copy.setNotes(request.notes());
        return toResponse(dvdCopyRepository.save(copy));
    }

    @Transactional
    public DvdCopyResponse updateStatus(Long id, DvdCopyStatusUpdateRequest request) {
        DvdCopy copy = dvdCopyRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("DVD copy not found"));
        if (copy.getStatus() == DvdCopyStatus.RENTED
                && request.status() != DvdCopyStatus.RETURNED_PENDING_INSPECTION
                && request.status() != DvdCopyStatus.LOST) {
            throw ApiException.conflict("Rented copies must be returned through the return workflow");
        }
        statusMachine.assertTransition(copy.getStatus(), request.status());
        copy.setStatus(request.status());
        if (request.notes() != null) {
            copy.setNotes(request.notes());
        }
        return toResponse(dvdCopyRepository.save(copy));
    }

    private DvdCopyResponse toResponse(DvdCopy copy) {
        return new DvdCopyResponse(
                copy.getId(),
                copy.getMovie().getId(),
                copy.getMovie().getTitle(),
                copy.getBarcode(),
                copy.getStatus(),
                copy.getNotes()
        );
    }
}
