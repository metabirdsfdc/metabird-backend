package org.verse.metabird.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.verse.metabird.model.Action;
import org.verse.metabird.model.History;
import org.verse.metabird.records.auth.Credentials;
import org.verse.metabird.records.retrieve.RetrieveType;
import org.verse.metabird.records.types.TypesRequestPayload;
import org.verse.metabird.utils.AsyncResultParser;
import org.verse.metabird.utils.Helpers;
import org.verse.metabird.xml.MetadataRequestBuilder;
import org.verse.metabird.xml.MetadataResponseParser;
import org.verse.metabird.xml.RetrieveRequestFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.verse.metabird.xml.MetadataRequestBuilder.describe;
import static org.verse.metabird.xml.MetadataRequestBuilder.list;

@Component
@RequiredArgsConstructor
public class MetadataService {

    private static final Duration RETRIEVE_POLL_INTERVAL = Duration.ofSeconds(1);
    private static final Duration DEPLOY_POLL_INTERVAL = Duration.ofSeconds(2);
    private static final Duration POLL_TIMEOUT = Duration.ofMinutes(5);
    private final HistoryService historyService;
    private final WebClient webClient;

    public Mono<String> retrieveMetadata(
            String userEmail,
            Credentials credentials,
            List<RetrieveType> retrieveTypes
    ) {
        return executeSoapRequest(
                Helpers.transformToActualEndpoint(credentials.endpoint()),
                RetrieveRequestFactory.create(
                        credentials,
                        retrieveTypes
                )
        )
                .flatMap(AsyncResultParser::extractAsyncId)
                .flatMap(retrieveId ->
                        pollRetrieveCompletion(
                                Helpers.transformToActualEndpoint(credentials.endpoint()),
                                credentials.sessionId(),
                                retrieveId
                        )
                )
                .flatMap(result ->
                        historyService.create(
                                        credentials.org(),
                                        History.builder()
                                                .email(userEmail)
                                                .action(Action.RETRIEVE)
                                                .success(true)
                                                .org(credentials.org())
                                                .details("SUCCESS")
                                                .build()
                                )
                                .thenReturn(result)
                )
                .onErrorResume(ex ->
                        historyService.create(
                                        credentials.org(),
                                        History.builder()
                                                .email(userEmail)
                                                .action(Action.RETRIEVE)
                                                .success(false)
                                                .org(credentials.org())
                                                .details(ex.getMessage())
                                                .build()
                                )
                                .then(Mono.error(ex))
                );
    }

    public Mono<List<String>> listMetadataTypes(Credentials credentials) {
        return executeSoapRequest(
                Helpers.transformToActualEndpoint(credentials.endpoint()),
                describe(
                        credentials.sessionId()
                )
        ).map(MetadataResponseParser::parseTypes);
    }

    public Mono<Object> listMetadataComponents(
            TypesRequestPayload payload,
            Credentials credentials
    ) {

        return executeSoapRequest(
                Helpers.transformToActualEndpoint(credentials.endpoint()),
                list(
                        credentials.sessionId(),
                        payload.type()
                )
        ).map(MetadataResponseParser::parseComponents);
    }

    public Mono<String> deployMetadata(
            String userEmail,
            Credentials credentials,
            String zipBase64
    ) {

        return executeSoapRequest(
                Helpers.transformToActualEndpoint(credentials.endpoint()),
                MetadataRequestBuilder.deploy(
                        credentials.sessionId(),
                        zipBase64,
                        false // checkOnly = false (actual deploy)
                )
        )
                .flatMap(AsyncResultParser::extractAsyncId)
                .flatMap(deployId ->
                        pollDeployCompletion(
                                Helpers.transformToActualEndpoint(credentials.endpoint()),
                                credentials.sessionId(),
                                deployId
                        )
                )
                .flatMap(result ->
                        historyService.create(
                                        credentials.org(),
                                        History.builder()
                                                .email(userEmail)
                                                .action(Action.DEPLOY)
                                                .success(true)
                                                .org(credentials.org())
                                                .details("SUCCESS")
                                                .build()
                                )
                                .thenReturn(result)
                )
                .onErrorResume(ex ->
                        historyService.create(
                                        credentials.org(),
                                        History.builder()
                                                .email(userEmail)
                                                .action(Action.DEPLOY)
                                                .success(false)
                                                .org(credentials.org())
                                                .details(ex.getMessage())
                                                .build()
                                )
                                .then(Mono.error(ex))
                );
    }

    private Mono<String> pollRetrieveCompletion(
            String endpoint,
            String sessionId,
            String retrieveId
    ) {
        return Flux.interval(RETRIEVE_POLL_INTERVAL)
                .flatMap(tick ->
                        executeSoapRequest(
                                endpoint,
                                MetadataRequestBuilder.checkRetrieve(
                                        sessionId,
                                        retrieveId
                                )
                        )
                )
                .filter(AsyncResultParser::isCompleted)
                .next()
                .timeout(POLL_TIMEOUT);
    }

    private Mono<String> pollDeployCompletion(
            String endpoint,
            String sessionId,
            String deployId
    ) {
        return Flux.interval(DEPLOY_POLL_INTERVAL)
                .flatMap(tick ->
                        executeSoapRequest(
                                endpoint,
                                MetadataRequestBuilder.checkDeploy(
                                        sessionId,
                                        deployId
                                )
                        )
                )
                .filter(AsyncResultParser::isCompleted)
                .next()
                .timeout(POLL_TIMEOUT);
    }


    private Mono<String> executeSoapRequest(
            String endpoint,
            String requestXml
    ) {
        return webClient.post()
                .uri(endpoint)
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .header("SOAPAction", "\"\"")
                .bodyValue(requestXml)
                .retrieve()
                .bodyToMono(String.class);
    }
}
