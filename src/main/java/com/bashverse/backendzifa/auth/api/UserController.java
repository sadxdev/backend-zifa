package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.UserInfoResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // or fine-grained RBAC as needed
    public UserInfoResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        // Optionally, call userService to fetch/enrich user profile data here

        return new UserInfoResponse(username, email, roles);
    }
}
