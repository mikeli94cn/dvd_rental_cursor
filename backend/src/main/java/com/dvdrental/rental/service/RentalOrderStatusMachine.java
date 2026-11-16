package com.dvdrental.rental.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.rental.entity.RentalOrderStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class RentalOrderStatusMachine {

    private final Map<RentalOrderStatus, Set<RentalOrderStatus>> transitions = new EnumMap<>(RentalOrderStatus.class);

    public RentalOrderStatusMachine() {
        transitions.put(RentalOrderStatus.PENDING, EnumSet.of(RentalOrderStatus.ACTIVE, RentalOrderStatus.CANCELLED));
        transitions.put(RentalOrderStatus.ACTIVE, EnumSet.of(RentalOrderStatus.COMPLETED, RentalOrderStatus.CANCELLED));
        transitions.put(RentalOrderStatus.COMPLETED, EnumSet.noneOf(RentalOrderStatus.class));
        transitions.put(RentalOrderStatus.CANCELLED, EnumSet.noneOf(RentalOrderStatus.class));
    }

    public void assertTransition(RentalOrderStatus from, RentalOrderStatus to) {
        Set<RentalOrderStatus> allowed = transitions.getOrDefault(from, EnumSet.noneOf(RentalOrderStatus.class));
        if (!allowed.contains(to)) {
            throw ApiException.conflict("Invalid rental order status transition from " + from + " to " + to);
        }
    }
}
