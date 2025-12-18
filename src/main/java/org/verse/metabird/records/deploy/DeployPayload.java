package org.verse.metabird.records.deploy;

@lombok.Builder
public record DeployPayload(String userId, String base64) {
}