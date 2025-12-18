package org.verse.metabird.vault;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppSecrets {

    private Mongo mongo;
    private Jwt jwt;
    private Redis redis;
    private Upstash upstash;
    private Frontend frontend;
    private Salesforce salesforce;

    public record Mongo(String uri) {
    }

    public record Jwt(String secret, long expiration, long refreshExpiration) {
    }

    public record Redis(String prefix) {
    }

    public record Upstash(String url, String token) {
    }

    public record Frontend(String url) {
    }

    public record Salesforce(String sandbox, String production) {
    }
}


