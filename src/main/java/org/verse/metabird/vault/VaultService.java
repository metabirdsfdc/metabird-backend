package org.verse.metabird.vault;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VaultService {

    private final VaultSecrets secrets;

    public String mongoUri() {
        return secrets.getMongo().getUri();
    }

    public String jwtSecret() {
        return secrets.getJwt().getSecret();
    }

    public String redisPassword() {
        return secrets.getRedis().getPassword();
    }

    public String upstashUrl() {
        return secrets.getUpstash().getUrl();
    }

    public String upstashToken() {
        return secrets.getUpstash().getToken();
    }

    public String frontendUrl() {
        return secrets.getFrontend().getUrl();
    }

    public long jwtExpiration() {
        return secrets.getJwt().getExpiration();
    }

    public long jwtRefreshExpiration() {
        return secrets.getJwt().getRefreshExpiration();
    }

    public String redisKeyPrefix() {
        return secrets.getRedis().getPrefix();
    }

    public String sandbox() {
        return secrets.getSalesforce().getSandbox();
    }

    public String production() {
        return secrets.getSalesforce().getProduction();
    }

}

