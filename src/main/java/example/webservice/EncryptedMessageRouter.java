package example.webservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class EncryptedMessageRouter {

	@Bean
	public RouterFunction<ServerResponse> route(EncryptedMessageHandler encryptedMessageHandler) {

		return RouterFunctions
			.route(GET("/hello").and(accept(MediaType.APPLICATION_JSON)), encryptedMessageHandler::hello)
				.andRoute(GET("/cesar/{key:[0-9]|1[0-9]|2[0-6]}").and(accept(MediaType.APPLICATION_JSON)), encryptedMessageHandler::cesar)
				.andRoute(GET("/allcesar/").and(accept(MediaType.APPLICATION_JSON)), encryptedMessageHandler::allCesar);

	}
}
