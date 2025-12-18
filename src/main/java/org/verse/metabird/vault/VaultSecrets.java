package org.verse.metabird.vault;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "")
@RefreshScope
public class VaultSecrets {

    private Mongo mongo;
    private Jwt jwt;
    private Redis redis;
    private Upstash upstash;
    private Frontend frontend;
    private Salesforce salesforce;

    @Data
    public static class Mongo {
        private String uri;
    }

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
        private long refreshExpiration;
    }

    @Data
    public static class Redis {
        private String password;
        private String prefix;
    }

    @Data
    public static class Upstash {
        private String url;
        private String token;
    }

    @Data
    public static class Frontend {
        private String url;
    }

    @Data
    public static class Salesforce {
        private String sandbox;
        private String production;
    }

}
