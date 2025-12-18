package org.verse.metabird.xml;

import org.verse.metabird.records.auth.Credentials;
import org.verse.metabird.records.retrieve.RetrieveType;
import org.verse.metabird.records.retrieve.RetrieveXMLPayload;

import java.util.List;

public final class RetrieveRequestFactory {

    private static final String API_VERSION = "52.0";

    private RetrieveRequestFactory() {
    }

    public static String create(
            Credentials credentials,
            List<RetrieveType> types
    ) {
        return RetrieveRequestBuilder.build(
                RetrieveXMLPayload.builder()
                        .sessionId(credentials.sessionId())
                        .apiVersion(API_VERSION)
                        .types(types)
                        .singlePackage(true)
                        .build()
        );
    }
}
