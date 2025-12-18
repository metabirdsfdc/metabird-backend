package org.verse.metabird.xml;

import org.verse.metabird.records.session.SalesforceSession;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public final class LoginRequestBuilder {

    private LoginRequestBuilder() {
    }

    public static String builder(String username, String password, String token) {
        return """
                <?xml version="1.0" encoding="utf-8" ?>
                                <env:Envelope xmlns:xsd="http:www.w3.org/2001/XMLSchema"
                                   xmlns:xsi="http:www.w3.org/2001/XMLSchema-instance"
                                   xmlns:env="http:schemas.xmlsoap.org/soap/envelope/">
                                 <env:Body>
                                   <n1:login xmlns:n1="urn:partner.soap.sforce.com">
                                     <n1:username>%s</n1:username>
                                     <n1:password>%s</n1:password>
                                   </n1:login>
                                 </env:Body>
                               </env:Envelope>
                """.formatted(username, password + token);
    }

    public static String build(String username, String password, String securityToken) {

        String passwordToken = password + securityToken;

        return """
                <?xml version="1.0" encoding="utf-8" ?>
                                <env:Envelope xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
                                 <env:Body>
                                   <n1:login xmlns:n1="urn:partner.soap.sforce.com">
                                     <n1:username>%s</n1:username>
                                     <n1:password>%s</n1:password>
                                   </n1:login>
                                 </env:Body>
                               </env:Envelope>
                """.formatted(username, passwordToken);
    }

    public static SalesforceSession parse(String xml) {

        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            XPath xpath = XPathFactory.newInstance().newXPath();

            String sessionId = (String) xpath.evaluate("//*[local-name()='sessionId']", doc, XPathConstants.STRING);
            String serverUrl = (String) xpath.evaluate("//*[local-name()='serverUrl']", doc, XPathConstants.STRING);
            String metadataServerUrl = (String) xpath.evaluate("//*[local-name()='metadataServerUrl']", doc, XPathConstants.STRING);
            String userId = (String) xpath.evaluate("//*[local-name()='userId'][1]", doc, XPathConstants.STRING);
            String organizationId = (String) xpath.evaluate("//*[local-name()='organizationId']", doc, XPathConstants.STRING);
            String userFullName = (String) xpath.evaluate("//*[local-name()='userFullName']", doc, XPathConstants.STRING);
            String userEmail = (String) xpath.evaluate("//*[local-name()='userEmail']", doc, XPathConstants.STRING);
            String organizationName = (String) xpath.evaluate("//*[local-name()='organizationName']", doc, XPathConstants.STRING);
            String sessionSecondsValidStr = (String) xpath.evaluate("//*[local-name()='sessionSecondsValid']", doc, XPathConstants.STRING);

            boolean sandbox = Boolean.parseBoolean(
                    (String) xpath.evaluate("//*[local-name()='sandbox']", doc, XPathConstants.STRING));

            boolean passwordExpired = Boolean.parseBoolean(
                    (String) xpath.evaluate("//*[local-name()='passwordExpired']", doc, XPathConstants.STRING));

            int sessionSecondsValid = Integer.parseInt(sessionSecondsValidStr);

            return SalesforceSession.builder()
                    .userId(userId)
                    .organizationId(organizationId)
                    .sessionId(sessionId)
                    .serverUrl(serverUrl)
                    .metadataServerUrl(metadataServerUrl)
                    .sandbox(sandbox)
                    .passwordExpired(passwordExpired)
                    .userFullName(userFullName)
                    .userEmail(userEmail)
                    .organizationName(organizationName)
                    .sessionSecondsValid(sessionSecondsValid)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Salesforce login response", e);
        }
    }
}
