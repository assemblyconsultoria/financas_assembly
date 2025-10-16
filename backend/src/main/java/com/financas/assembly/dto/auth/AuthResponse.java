package com.financas.assembly.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for authentication responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String email;
    private String name;
    private Set<String> roles;

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn,
                        String email, String name, Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }
}
