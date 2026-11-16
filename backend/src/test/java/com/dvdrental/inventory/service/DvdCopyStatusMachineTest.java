package com.dvdrental.inventory.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DvdCopyStatusMachineTest {

    private final DvdCopyStatusMachine machine = new DvdCopyStatusMachine();

    @Test
    void allowsAvailableToRented() {
        assertThatCode(() -> machine.assertTransition(DvdCopyStatus.AVAILABLE, DvdCopyStatus.RENTED))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsInvalidTransition() {
        assertThatThrownBy(() -> machine.assertTransition(DvdCopyStatus.AVAILABLE, DvdCopyStatus.RETURNED_PENDING_INSPECTION))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void allowsInspectionOutcomes() {
        assertThatCode(() -> machine.assertTransition(
                DvdCopyStatus.RETURNED_PENDING_INSPECTION, DvdCopyStatus.AVAILABLE))
                .doesNotThrowAnyException();
        assertThatCode(() -> machine.assertTransition(
                DvdCopyStatus.RETURNED_PENDING_INSPECTION, DvdCopyStatus.DAMAGED))
                .doesNotThrowAnyException();
    }
}
