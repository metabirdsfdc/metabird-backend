package org.verse.metabird.routes;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.verse.metabird.handler.HistoryHandler;

@Component
@RequiredArgsConstructor
@NullMarked
public class HistoryRoutes {

    @Bean
    public RouterFunction<ServerResponse> routesForHistory(HistoryHandler handler) {
        return RouterFunctions.route()
                .GET("/api/history", handler::getAll)
                .build();
    }

}
