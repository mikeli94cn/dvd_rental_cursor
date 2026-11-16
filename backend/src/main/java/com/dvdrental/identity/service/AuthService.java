package com.dvdrental.identity.service;

import com.dvdrental.common.error.ApiException;
import com.dvdrental.customer.entity.Customer;
import com.dvdrental.customer.repository.CustomerRepository;
import com.dvdrental.identity.dto.LoginRequest;
import com.dvdrental.identity.dto.RegisterRequest;
import com.dvdrental.identity.dto.UserResponse;
import com.dvdrental.identity.entity.Role;
import com.dvdrental.identity.entity.UserAccount;
import com.dvdrental.identity.repository.RoleRepository;
import com.dvdrental.identity.repository.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserAccountRepository userAccountRepository,
                       RoleRepository roleRepository,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw ApiException.conflict("Email already registered");
        }
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> ApiException.badRequest("CUSTOMER role missing"));

        UserAccount user = new UserAccount();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.getRoles().add(customerRole);
        userAccountRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setPhone(request.phone());
        customerRepository.save(customer);

        return toResponse(user, customer);
    }

    public UserResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return currentUser();
    }

    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser() {
        String email = com.dvdrental.common.security.SecurityUtils.currentEmail();
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> ApiException.unauthorized("User not found"));
        Customer customer = customerRepository.findByUserId(user.getId()).orElse(null);
        return toResponse(user, customer);
    }

    private UserResponse toResponse(UserAccount user, Customer customer) {
        List<String> roles = user.getRoles().stream().map(Role::getName).sorted().toList();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                roles,
                customer != null ? customer.getId() : null,
                customer != null ? customer.getFirstName() : null,
                customer != null ? customer.getLastName() : null,
                customer != null ? customer.getPhone() : null
        );
    }
}
