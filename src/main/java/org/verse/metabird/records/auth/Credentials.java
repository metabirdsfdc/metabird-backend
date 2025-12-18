package org.verse.metabird.records.auth;

@lombok.Builder
public record Credentials(String org, String sessionId, String endpoint) {
}