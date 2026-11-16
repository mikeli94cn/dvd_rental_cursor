package com.dvdrental.reporting.controller;

import com.dvdrental.reporting.dto.DashboardStatsResponse;
import com.dvdrental.reporting.dto.OverdueItemResponse;
import com.dvdrental.reporting.service.ReportingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/reports")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/dashboard")
    public DashboardStatsResponse dashboard() {
        return reportingService.dashboard();
    }

    @GetMapping("/overdue")
    public Page<OverdueItemResponse> overdue(@PageableDefault(size = 20) Pageable pageable) {
        return reportingService.overdueReport(pageable);
    }
}
