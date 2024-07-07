package com.libra_s.libraS.service;

import com.libra_s.libraS.config.ApiKeyAuthentication;
import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.enums.Role;
import com.libra_s.libraS.dtos.LoginUserDto;
import com.libra_s.libraS.dtos.RegisterUserDto;
import com.libra_s.libraS.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {
    private final AppUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public static final String AUTH_TOKEN_HEADER_NAME = "API-KEY";

    @Value("${api.key}")
    private String apiKey;

    private static String API_KEY;

    @PostConstruct
    public void init() {
        API_KEY = this.apiKey;
    }

    public AuthenticationService(
            AppUserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser signup(RegisterUserDto input) {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);

        AppUser user = new AppUser();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setDisplayname(input.getDisplayname());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public AppUser authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String requestApiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (requestApiKey == null || !requestApiKey.equals(API_KEY)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(requestApiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
