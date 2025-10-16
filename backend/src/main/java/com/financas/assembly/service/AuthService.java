package com.financas.assembly.service;

import com.financas.assembly.dto.auth.AuthResponse;
import com.financas.assembly.dto.auth.LoginRequest;
import com.financas.assembly.dto.auth.RefreshTokenRequest;
import com.financas.assembly.dto.auth.RegisterRequest;
import com.financas.assembly.entity.RefreshToken;
import com.financas.assembly.entity.Role;
import com.financas.assembly.entity.User;
import com.financas.assembly.repository.RefreshTokenRepository;
import com.financas.assembly.repository.RoleRepository;
import com.financas.assembly.repository.UserRepository;
import com.financas.assembly.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for authentication operations.
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getEmail());

        // Save refresh token
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        saveRefreshToken(user, refreshToken);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        user.resetLoginAttempts();
        userRepository.save(user);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                refreshToken,
                tokenProvider.getExpirationMs(),
                user.getEmail(),
                user.getName(),
                roles
        );
    }

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email já está em uso");
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setActive(true);
        user.setEmailVerified(true); // Auto-verify for now, implement email verification later

        // Assign default role (USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrado"));
        user.getRoles().add(userRole);

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = tokenProvider.generateTokenFromUsername(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                refreshToken,
                tokenProvider.getExpirationMs(),
                user.getEmail(),
                user.getName(),
                roles
        );
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!tokenProvider.validateToken(requestRefreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        // Get username from token
        String email = tokenProvider.getUsernameFromToken(requestRefreshToken);

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

        // Check if token is revoked
        if (refreshToken.getRevoked()) {
            throw new RuntimeException("Refresh token foi revogado");
        }

        // Check if token is expired
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        // Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Generate new access token
        String newAccessToken = tokenProvider.generateTokenFromUsername(email);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                newAccessToken,
                requestRefreshToken,
                tokenProvider.getExpirationMs(),
                user.getEmail(),
                user.getName(),
                roles
        );
    }

    /**
     * Logout user by revoking refresh token
     */
    @Transactional
    public void logout(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        token.ifPresent(rt -> {
            rt.setRevoked(true);
            rt.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(rt);
        });
    }

    /**
     * Save refresh token to database
     */
    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshExpirationMs() / 1000));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }
}
