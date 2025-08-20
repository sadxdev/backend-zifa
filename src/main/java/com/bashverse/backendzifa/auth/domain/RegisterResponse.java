package com.bashverse.backendzifa.auth.domain;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID id;        // local DB id
    private String username;
    private String email;
    private short status;   // pending, active, disabled
}

