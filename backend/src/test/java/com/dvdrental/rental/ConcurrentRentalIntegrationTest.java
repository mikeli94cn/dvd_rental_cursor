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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentRentalIntegrationTest extends AbstractIntegrationTest {

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
    private String customer1Email;
    private String customer2Email;

    @BeforeEach
    void setUp() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        customer1Email = "race1@example.com";
        customer2Email = "race2@example.com";
        createCustomer(customer1Email, "Race", "One", customerRole);
        createCustomer(customer2Email, "Race", "Two", customerRole);

        Movie movie = new Movie();
        movie.setTitle("Race Film");
        movie.setDailyRate(new BigDecimal("1.00"));
        movie.setRentalDaysDefault(2);
        movie = movieRepository.save(movie);

        DvdCopy copy = new DvdCopy();
        copy.setMovie(movie);
        copy.setBarcode("RACE-COPY-001");
        copy.setStatus(DvdCopyStatus.AVAILABLE);
        copy = dvdCopyRepository.save(copy);
        copyId = copy.getId();
    }

    @Test
    void onlyOneConcurrentRentalSucceeds() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();

        Future<?> f1 = executor.submit(() -> raceAttempt(customer1Email, start, successes, failures));
        Future<?> f2 = executor.submit(() -> raceAttempt(customer2Email, start, successes, failures));
        start.countDown();
        f1.get();
        f2.get();
        executor.shutdownNow();

        assertThat(successes.get()).isEqualTo(1);
        assertThat(failures.get()).isEqualTo(1);
        assertThat(dvdCopyRepository.findById(copyId).orElseThrow().getStatus())
                .isEqualTo(DvdCopyStatus.RENTED);
    }

    private void raceAttempt(String email, CountDownLatch start, AtomicInteger successes, AtomicInteger failures) {
        try {
            start.await();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            email, "n/a", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));
            rentalService.createRental(new CreateRentalRequest(List.of(copyId), 2));
            successes.incrementAndGet();
        } catch (Exception ex) {
            failures.incrementAndGet();
        } finally {
            SecurityContextHolder.clearContext();
        }
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
}
