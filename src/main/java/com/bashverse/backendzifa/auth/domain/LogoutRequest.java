package com.bashverse.backendzifa.auth.domain;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
