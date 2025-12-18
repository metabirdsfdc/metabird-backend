package org.verse.metabird.service;


import org.verse.metabird.records.auth.AuthResponse;
import org.verse.metabird.records.auth.LoginPayload;
import org.verse.metabird.records.auth.SignUpPayload;
import reactor.core.publisher.Mono;

public interface OAuthService {

    Mono<AuthResponse> login(LoginPayload request);

    Mono<Object> signup(SignUpPayload request);

}
