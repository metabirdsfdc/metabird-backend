package org.verse.metabird.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.verse.metabird.jwt.JwtService;
import org.verse.metabird.model.Client;
import org.verse.metabird.records.auth.AuthResponse;
import org.verse.metabird.records.auth.LoginPayload;
import org.verse.metabird.records.auth.SignUpPayload;
import org.verse.metabird.repository.ClientRepository;
import org.verse.metabird.service.OAuthService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;


    private AuthResponse buildAuthResponse(Client client) {
        String accessToken = jwtService.generateToken(
                Map.of("roles", client.getAuthorities()),
                client.getUsername()
        );
        String refreshToken = jwtService.generateRefreshToken(client.getUsername());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(
                        AuthResponse.User.builder()
                                .fullName(client.getFullName())
                                .username(client.getUsername())
                                .build()
                )
                .build();
    }

    @Override
    public Mono<AuthResponse> login(LoginPayload request) {
        return clientRepository.findByUsername(request.username())
                .switchIfEmpty(
                        Mono.error(new RuntimeException("Invalid username or password"))
                )
                .flatMap(client -> {
                    if (!passwordEncoder.matches(request.password(), client.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid username or password"));
                    }
                    return Mono.just(buildAuthResponse(client));
                });
    }

    @Override
    public Mono<Object> signup(SignUpPayload request) {
        return clientRepository.existsByUsername(request.username())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(Map.of("error", "User already exists"));
                    }
                    Client client = Client.builder()
                            .fullName(request.fullName())
                            .username(request.username())
                            .password(Objects.requireNonNull(passwordEncoder.encode(request.password())))
                            .roles(List.of("ROLE_USER"))
                            .build();

                    return clientRepository.save(client)
                            .map(this::buildAuthResponse);
                })

                .onErrorResume(ex ->
                        Mono.just(Map.of("error", "Signup failed: " + ex.getMessage()))
                );
    }

}
