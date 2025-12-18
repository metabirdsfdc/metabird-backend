package org.verse.metabird.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.verse.metabird.records.auth.LoginPayload;
import org.verse.metabird.records.auth.SignUpPayload;
import org.verse.metabird.service.OAuthService;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final OAuthService authService;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginPayload.class)
                .flatMap(req -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(authService.login(req), LoginPayload.class));
    }

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request.bodyToMono(SignUpPayload.class)
                .flatMap(req -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(authService.signup(req), SignUpPayload.class));
    }


}

