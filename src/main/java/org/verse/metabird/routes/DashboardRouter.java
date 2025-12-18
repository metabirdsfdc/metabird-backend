package org.verse.metabird.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.verse.metabird.handler.DashboardHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class DashboardRouter {

    @Bean
    public RouterFunction<ServerResponse> dashboardRoutes(DashboardHandler handler) {
        return route(GET("/api/dashboard/deployments"), handler::dashboard);
    }
}
