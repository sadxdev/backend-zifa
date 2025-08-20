package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.RegisterRequest;
import com.bashverse.backendzifa.auth.domain.RegisterResponse;
import com.bashverse.backendzifa.auth.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerService.registerUser(request);
        return ResponseEntity.ok(response);
    }
}
