package org.verse.metabird.records.auth;

import lombok.Builder;

@Builder
public record SignUpPayload(String fullName, String username, String password) {
}