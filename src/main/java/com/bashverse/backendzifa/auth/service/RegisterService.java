package com.bashverse.backendzifa.auth.service;

import com.bashverse.backendzifa.auth.domain.RegisterRequest;
import com.bashverse.backendzifa.auth.domain.RegisterResponse;
import com.bashverse.backendzifa.auth.infra.persistence.UserEntity;
import com.bashverse.backendzifa.auth.infra.repository.UserRepository;
import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final KeycloakAdminClient keycloakAdminClient;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        // Validate input not null (extra guard)
        Objects.requireNonNull(request, "RegisterRequest must not be null");
        Objects.requireNonNull(request.getEmail(), "Email must not be null");
        Objects.requireNonNull(request.getUsername(), "Username must not be null");
        Objects.requireNonNull(request.getPassword(), "Password must not be null");

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Create user in Keycloak
        String keycloakUserId = keycloakAdminClient.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        String hash = passwordEncoder.encode(request.getPassword());

        UserEntity entity = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(hash)
                .status((short) 1)
                .keycloakId(java.util.UUID.fromString(keycloakUserId))
                .build();

        UserEntity saved = userRepository.save(entity);

        return RegisterResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .status(saved.getStatus())
                .build();
    }
}
