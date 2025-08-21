package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.UserInfoResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInfoController {

    @GetMapping("/auth/userinfo")
    public UserInfoResponse getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        // Extract relevant claims from JWT issued by Keycloak
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        return new UserInfoResponse(username, email, roles);
    }
}
