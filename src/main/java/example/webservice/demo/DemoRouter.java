package example.webservice.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration(proxyBeanMethods = false)
public class DemoRouter {


    @Bean
    public RouterFunction<ServerResponse> demoRouterHandler(DemoHandler demoHandler) {
        return RouterFunctions.route(GET("/demo/hello"), demoHandler::sayHello);
    }
}
