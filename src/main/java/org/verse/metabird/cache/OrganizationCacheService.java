package org.verse.metabird.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.verse.metabird.cache.base.AbstractUpstashListCacheService;
import org.verse.metabird.records.session.SalesforceSession;
import org.verse.metabird.vault.VaultService;

import java.util.function.Predicate;

@Component
public class OrganizationCacheService
        extends AbstractUpstashListCacheService<String, SalesforceSession, String> {

    private final VaultService vaultService;

    public OrganizationCacheService(
            @Qualifier("upstashRedisClient") WebClient upstashClient,
            ObjectMapper objectMapper,
            VaultService vaultService
    ) {
        super(
                upstashClient,
                objectMapper,
                new TypeReference<>() {
                }
        );
        this.vaultService = vaultService;
    }

    @Override
    protected String buildKey(String userId) {
        return vaultService.redisKeyPrefix() + userId;
    }

    @Override
    protected Predicate<SalesforceSession> matchByIdentifier(String userId) {
        return session -> session.getUserId().equalsIgnoreCase(userId);
    }

    @Override
    protected Predicate<SalesforceSession> matchByValue(SalesforceSession value) {
        return session ->
                session.getUserEmail().equalsIgnoreCase(value.getUserEmail());
    }
}
