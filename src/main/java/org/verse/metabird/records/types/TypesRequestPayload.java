package org.verse.metabird.records.types;

import lombok.Builder;

@Builder
public record TypesRequestPayload(String userId, String type) {
}