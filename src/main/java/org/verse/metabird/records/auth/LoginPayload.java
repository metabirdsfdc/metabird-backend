package org.verse.metabird.records.auth;

@lombok.Builder
public record LoginPayload(String username, String password) {
}