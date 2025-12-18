package org.verse.metabird.vault;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VaultService {

    private final AppSecrets secrets;

    public String mongoUri() {
        String uri = secrets.getMongo().uri();

        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException("Mongo URI is missing");
        }

        return uri.trim().replaceAll("^\"|\"$", "");
    }


    public String jwtSecret() {
        return secrets.getJwt().secret();
    }

    public String upstashUrl() {
        return secrets.getUpstash().url();
    }

    public String upstashToken() {
        return secrets.getUpstash().token();
    }

    public String frontendUrl() {
        return secrets.getFrontend().url();
    }

    public long jwtExpiration() {
        return secrets.getJwt().expiration();
    }

    public long jwtRefreshExpiration() {
        return secrets.getJwt().refreshExpiration();
    }

    public String redisKeyPrefix() {
        return secrets.getRedis().prefix();
    }

    public String sandbox() {
        return secrets.getSalesforce().sandbox();
    }

    public String production() {
        return secrets.getSalesforce().production();
    }

}

