package com.dvdrental.rental;

import com.dvdrental.catalog.entity.Movie;
import com.dvdrental.catalog.repository.MovieRepository;
import com.dvdrental.customer.entity.Customer;
import com.dvdrental.customer.repository.CustomerRepository;
import com.dvdrental.identity.entity.Role;
import com.dvdrental.identity.entity.UserAccount;
import com.dvdrental.identity.repository.RoleRepository;
import com.dvdrental.identity.repository.UserAccountRepository;
import com.dvdrental.inventory.entity.DvdCopy;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.repository.DvdCopyRepository;
import com.dvdrental.rental.dto.CreateRentalRequest;
import com.dvdrental.rental.dto.RentalOrderResponse;
import com.dvdrental.rental.dto.ReturnItemRequest;
import com.dvdrental.rental.entity.RentalOrderStatus;
import com.dvdrental.rental.service.RentalService;
import com.dvdrental.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DvdCopyRepository dvdCopyRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long copyId;
    private String customerEmail;
    private String otherEmail;
    private String staffEmail;

    @BeforeEach
    void setUp() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();
        Role staffRole = roleRepository.findByName("STAFF").orElseThrow();

        customerEmail = "renter@example.com";
        otherEmail = "other@example.com";
        staffEmail = "clerk@example.com";
        createCustomer(customerEmail, "Rent", "Er", customerRole);
        createCustomer(otherEmail, "Other", "Person", customerRole);
        createStaff(staffEmail, staffRole);

        Movie movie = new Movie();
        movie.setTitle("Flow Film");
        movie.setDailyRate(new BigDecimal("2.00"));
        movie.setRentalDaysDefault(3);
        movie = movieRepository.save(movie);

        DvdCopy copy = new DvdCopy();
        copy.setMovie(movie);
        copy.setBarcode("FLOW-001");
        copy.setStatus(DvdCopyStatus.AVAILABLE);
        copyId = dvdCopyRepository.save(copy).getId();
    }

    @Test
    void customerCanRentAndStaffCanReturn() {
        asUser(customerEmail, "ROLE_CUSTOMER");
        RentalOrderResponse order = rentalService.createRental(new CreateRentalRequest(List.of(copyId), 3));
        assertThat(order.status()).isEqualTo(RentalOrderStatus.ACTIVE);
        assertThat(order.totalAmount()).isEqualByComparingTo("6.00");
        assertThat(dvdCopyRepository.findById(copyId).orElseThrow().getStatus()).isEqualTo(DvdCopyStatus.RENTED);

        Long itemId = order.items().getFirst().id();
        asUser(staffEmail, "ROLE_STAFF");
        RentalOrderResponse returned = rentalService.confirmReturn(
                new ReturnItemRequest(itemId, DvdCopyStatus.AVAILABLE, "OK"));
        assertThat(returned.status()).isEqualTo(RentalOrderStatus.COMPLETED);
        assertThat(dvdCopyRepository.findById(copyId).orElseThrow().getStatus()).isEqualTo(DvdCopyStatus.AVAILABLE);
    }

    @Test
    void customerCannotViewAnotherCustomersRental() {
        asUser(customerEmail, "ROLE_CUSTOMER");
        RentalOrderResponse order = rentalService.createRental(new CreateRentalRequest(List.of(copyId), 2));

        asUser(otherEmail, "ROLE_CUSTOMER");
        assertThatThrownBy(() -> rentalService.getRental(order.id()))
                .hasMessageContaining("another customer's rental");
    }

    private void asUser(String email, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, "n/a", List.of(new SimpleGrantedAuthority(role))));
    }

    private void createCustomer(String email, String first, String last, Role role) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Password123!"));
        user.getRoles().add(role);
        userAccountRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(first);
        customer.setLastName(last);
        customerRepository.save(customer);
    }

    private void createStaff(String email, Role role) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Password123!"));
        user.getRoles().add(role);
        userAccountRepository.save(user);
    }
}
