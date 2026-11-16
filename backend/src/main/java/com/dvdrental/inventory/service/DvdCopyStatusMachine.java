package com.dvdrental.inventory.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class DvdCopyStatusMachine {

    private final Map<DvdCopyStatus, Set<DvdCopyStatus>> transitions = new EnumMap<>(DvdCopyStatus.class);

    public DvdCopyStatusMachine() {
        transitions.put(DvdCopyStatus.AVAILABLE, EnumSet.of(
                DvdCopyStatus.RENTED, DvdCopyStatus.MAINTENANCE, DvdCopyStatus.LOST, DvdCopyStatus.DAMAGED));
        transitions.put(DvdCopyStatus.RENTED, EnumSet.of(
                DvdCopyStatus.RETURNED_PENDING_INSPECTION, DvdCopyStatus.LOST));
        transitions.put(DvdCopyStatus.RETURNED_PENDING_INSPECTION, EnumSet.of(
                DvdCopyStatus.AVAILABLE, DvdCopyStatus.DAMAGED, DvdCopyStatus.MAINTENANCE, DvdCopyStatus.LOST));
        transitions.put(DvdCopyStatus.DAMAGED, EnumSet.of(
                DvdCopyStatus.MAINTENANCE, DvdCopyStatus.LOST, DvdCopyStatus.AVAILABLE));
        transitions.put(DvdCopyStatus.MAINTENANCE, EnumSet.of(
                DvdCopyStatus.AVAILABLE, DvdCopyStatus.DAMAGED, DvdCopyStatus.LOST));
        transitions.put(DvdCopyStatus.LOST, EnumSet.of(DvdCopyStatus.AVAILABLE));
    }

    public void assertTransition(DvdCopyStatus from, DvdCopyStatus to) {
        Set<DvdCopyStatus> allowed = transitions.getOrDefault(from, EnumSet.noneOf(DvdCopyStatus.class));
        if (!allowed.contains(to)) {
            throw ApiException.conflict("Invalid DVD status transition from " + from + " to " + to);
        }
    }
}
