package com.dvdrental.rental.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.rental.entity.RentalOrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalOrderStatusMachineTest {

    private final RentalOrderStatusMachine machine = new RentalOrderStatusMachine();

    @Test
    void allowsActiveToCompleted() {
        assertThatCode(() -> machine.assertTransition(RentalOrderStatus.ACTIVE, RentalOrderStatus.COMPLETED))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsCompletedToActive() {
        assertThatThrownBy(() -> machine.assertTransition(RentalOrderStatus.COMPLETED, RentalOrderStatus.ACTIVE))
                .isInstanceOf(ApiException.class);
    }
}
