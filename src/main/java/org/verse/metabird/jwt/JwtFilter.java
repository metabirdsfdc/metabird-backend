package org.verse.metabird.jwt;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.verse.metabird.config.UserDetailsService;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@NullMarked
public class JwtFilter implements WebFilter {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/**"
    );
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        boolean excluded = EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (excluded) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // ✅ DO NOT BLOCK IF TOKEN IS MISSING
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);

        return Mono.fromCallable(() -> jwtService.extractUsername(jwt))
                .flatMap(username -> doAuthenticate(username, jwt))
                .flatMap(authentication ->
                        chain.filter(exchange)
                                .contextWrite(
                                        ReactiveSecurityContextHolder
                                                .withAuthentication(authentication)
                                )
                )
                .onErrorResume(ex -> chain.filter(exchange));
    }

    private Mono<UsernamePasswordAuthenticationToken> doAuthenticate(
            String username,
            String jwt
    ) {
        return userDetailsService.findByUsername(username)
                .filter(user ->
                        jwtService.validateToken(jwt, user.getUsername())
                )
                .map(this::buildAuthentication);
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(UserDetails user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }
}
