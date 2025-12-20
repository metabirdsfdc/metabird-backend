package org.verse.metabird.routes;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.verse.metabird.handler.MetadataHandler;

@Component
public class MetadataRoutes {

    @Bean
    public RouterFunction<@NonNull ServerResponse> routerFunction(MetadataHandler handler) {
        return RouterFunctions.route()
                .POST("/api/metadata/types", handler::fetchMetadataTypes)
                .POST("/api/metadata/components", handler::fetchMetadataComponents)
                .POST("/api/metadata/retrieve", handler::pullMetadata)
                .POST("/api/metadata/deploy", handler::pushMetadata)
                .POST("/api/metadata/deployment/execute", handler::executeMetadataDeployment)
                .build();
    }

}
