package com.dvdrental.common.config;

import com.dvdrental.catalog.entity.Genre;
import com.dvdrental.catalog.entity.Movie;
import com.dvdrental.catalog.repository.GenreRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final SeedProperties seedProperties;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(SeedProperties seedProperties,
                         RoleRepository roleRepository,
                         UserAccountRepository userAccountRepository,
                         CustomerRepository customerRepository,
                         GenreRepository genreRepository,
                         MovieRepository movieRepository,
                         DvdCopyRepository dvdCopyRepository,
                         PasswordEncoder passwordEncoder) {
        this.seedProperties = seedProperties;
        this.roleRepository = roleRepository;
        this.userAccountRepository = userAccountRepository;
        this.customerRepository = customerRepository;
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedProperties.isEnabled()) {
            return;
        }
        if (userAccountRepository.existsByEmailIgnoreCase("admin@dvdrental.local")) {
            return;
        }
        log.info("Seeding development data (admin/staff/sample catalog)");

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role staffRole = roleRepository.findByName("STAFF").orElseThrow();
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        createStaffUser("admin@dvdrental.local", "Admin123!", Set.of(adminRole, staffRole));
        createStaffUser("staff@dvdrental.local", "Staff123!", Set.of(staffRole));
        createCustomerUser("customer@dvdrental.local", "Customer123!", "Demo", "Customer", customerRole);

        Genre action = upsertGenre("Action");
        Genre drama = upsertGenre("Drama");
        Genre comedy = upsertGenre("Comedy");

        Movie matrix = createMovie("The Matrix", "A computer hacker learns about the true nature of reality.",
                1999, new BigDecimal("2.99"), 3, Set.of(action));
        Movie godfather = createMovie("The Godfather", "An organized crime dynasty's aging patriarch transfers control.",
                1972, new BigDecimal("3.49"), 5, Set.of(drama));
        Movie groundhog = createMovie("Groundhog Day", "A weatherman relives the same day again and again.",
                1993, new BigDecimal("2.49"), 3, Set.of(comedy));

        createCopy(matrix, "DVD-MATRIX-001");
        createCopy(matrix, "DVD-MATRIX-002");
        createCopy(godfather, "DVD-GODFATHER-001");
        createCopy(groundhog, "DVD-GROUNDHOG-001");
        createCopy(groundhog, "DVD-GROUNDHOG-002");
    }

    private void createStaffUser(String email, String password, Set<Role> roles) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(roles);
        userAccountRepository.save(user);
    }

    private void createCustomerUser(String email, String password, String first, String last, Role role) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.getRoles().add(role);
        userAccountRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(first);
        customer.setLastName(last);
        customer.setPhone("555-0100");
        customerRepository.save(customer);
    }

    private Genre upsertGenre(String name) {
        return genreRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Genre genre = new Genre();
            genre.setName(name);
            return genreRepository.save(genre);
        });
    }

    private Movie createMovie(String title, String description, int year, BigDecimal rate, int days, Set<Genre> genres) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setReleaseYear(year);
        movie.setDailyRate(rate);
        movie.setRentalDaysDefault(days);
        movie.setGenres(genres);
        return movieRepository.save(movie);
    }

    private void createCopy(Movie movie, String barcode) {
        DvdCopy copy = new DvdCopy();
        copy.setMovie(movie);
        copy.setBarcode(barcode);
        copy.setStatus(DvdCopyStatus.AVAILABLE);
        dvdCopyRepository.save(copy);
    }
}
