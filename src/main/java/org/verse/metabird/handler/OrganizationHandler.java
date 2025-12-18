package org.verse.metabird.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.verse.metabird.cache.OrganizationCacheService;
import org.verse.metabird.handler.base.AbstractSessionHandler;
import org.verse.metabird.handler.contract.Organization;
import org.verse.metabird.records.auth.OAuthCredRequest;
import org.verse.metabird.records.session.SalesforceSession;
import org.verse.metabird.vault.VaultService;
import org.verse.metabird.xml.LoginRequestBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class OrganizationHandler
        extends AbstractSessionHandler
        implements Organization {

    private final OrganizationCacheService cache;
    private final VaultService vault;
    private final WebClient client;

    public OrganizationHandler(OrganizationCacheService cache,
                               VaultService vault,
                               @Qualifier("organizationWebClient")
                               WebClient client) {
        this.cache = cache;
        this.vault = vault;
        this.client = client;
    }

    @Override
    public Mono<ServerResponse> create(ServerRequest request) {
        return authenticatedUser(request)
                .flatMap(user ->
                        request.bodyToMono(OAuthCredRequest.class)
                                .flatMap(req -> authenticateAndStore(user, req))
                )
                .flatMap(this::ok);
    }

    @Override
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return authenticatedUser(request)
                .flatMap(user ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(cache.getAll(user), SalesforceSession.class)
                );
    }

    @Override
    public Mono<ServerResponse> read(ServerRequest request) {
        return authenticatedUser(request)
                .flatMap(user -> cache.read(user, identifier(request)))
                .flatMap(this::ok)
                .switchIfEmpty(notFound());
    }

    @Override
    public Mono<ServerResponse> delete(ServerRequest request) {
        return authenticatedUser(request)
                .flatMap(user -> cache.delete(user, identifier(request)))
                .flatMap(this::ok);
    }

    private Mono<Map<String, String>> authenticateAndStore(
            String user,
            OAuthCredRequest req
    ) {
        return client.post()
                .uri(authUrl(req.getOrgType()))
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .header("SOAPAction", "login")
                .bodyValue(LoginRequestBuilder.build(
                        req.getUsername(),
                        req.getPassword(),
                        req.getSecurityToken()
                ))
                .retrieve()
                .bodyToMono(String.class)
                .map(LoginRequestBuilder::parse)
                .flatMap(session ->
                        cache.create(user, session)
                                .thenReturn(response(session))
                );
    }

    private Map<String, String> response(SalesforceSession s) {
        return Map.of(
                "status", "SUCCESS",
                "organizationId", s.getOrganizationId(),
                "userEmail", s.getUserEmail()
        );
    }

    private String authUrl(String type) {
        return "sandbox".equalsIgnoreCase(type)
                ? vault.sandbox()
                : vault.production();
    }
}
