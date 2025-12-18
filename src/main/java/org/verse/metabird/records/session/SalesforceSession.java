package org.verse.metabird.records.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesforceSession implements Serializable {

    private String userId;
    private String organizationId;

    private String sessionId;
    private String serverUrl;
    private String metadataServerUrl;

    private boolean sandbox;
    private boolean passwordExpired;

    private String userFullName;
    private String userEmail;
    private String organizationName;

    private int sessionSecondsValid;  // e.g., 7200

}
