package com.troy.security.auth;

import com.troy.security.config.JwtService;
import com.troy.security.token.Token;
import com.troy.security.token.TokenType;
import com.troy.security.token.token.TokenRepository;
import com.troy.security.user.Role;
import com.troy.security.user.User;
import com.troy.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                //.role(Role.USER)
                .role(Role.valueOf(request.getRole().name()))
                .build();
        var jwtToken = jwtService.generateToken(user);
        repository.save(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate( AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user.getId());
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                //.user(user)
                .userId(user.getId())
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

    }

    private void revokeAllUserTokens(String userId) {
        var validUserTokens = tokenRepository.findAllByUserIdAndExpiredIsFalseOrRevokedIsFalse(userId);
        if (validUserTokens.isEmpty() || Objects.isNull(validUserTokens))
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
