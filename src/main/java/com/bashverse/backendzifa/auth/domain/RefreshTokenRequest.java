package com.bashverse.backendzifa.auth.domain;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
