package org.verse.metabird.routes;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@NullMarked
public class PingRoutes {

    @Bean
    public RouterFunction<ServerResponse> pingerRoutes() {
        return RouterFunctions.route()
                .GET("/api/ping", this::ping)
                .build();
    }

    public Mono<ServerResponse> ping(ServerRequest request) {
        return ServerResponse.ok()
                .bodyValue("Pong from Salesforce Metadata Tooling");
    }
}
