package example.webservice.controllermultiparams;

import example.webservice.EncryptedMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false) public class EncryptedMessageRouterWithParameters {
    @Bean(name="withparams")
    public RouterFunction<ServerResponse> route(HandlerWithParams handler) {
        return RouterFunctions
                .route(POST("/cesar3keypost").and(accept(MediaType.APPLICATION_JSON)), handler::hello);
    }

}
