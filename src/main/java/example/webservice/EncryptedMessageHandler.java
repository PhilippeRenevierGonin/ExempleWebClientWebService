package example.webservice;

import example.data.Message;
import example.webservice.features.Cesar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * Le compossant "handler" qui propose 5 méthodes pour 5 traitements de requêtes
 */
@Component
public class EncryptedMessageHandler {

	@Autowired
	Cesar crypt;

	public Mono<ServerResponse> hello(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(new Message("Hello, Spring!")));
	}


	public Mono<ServerResponse> cesar(ServerRequest request) {
		String toCode = request.queryParam("val").orElse("no string");
		toCode.toLowerCase();
		int decalage = Integer.parseInt(request.pathVariable("key"));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new Message(crypt.encodeCesar(toCode, decalage))));
	}


	public Mono<ServerResponse> allCesar(ServerRequest request) {
		String toCode = request.queryParam("val").orElse("no string");
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(crypt.generateAllCesar(toCode)));
	}


	public Mono<ServerResponse> allCesarPost(ServerRequest serverRequest) {
		Mono<String> param = serverRequest.bodyToMono(String.class) ; // on n'a pas le droit de faire block ici
		return param.flatMap(msg -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(crypt.generateAllCesar(msg))));
	}

	public Mono<ServerResponse> allCesarPostObj(ServerRequest serverRequest) {
		Mono<Message> param = serverRequest.bodyToMono(Message.class) ; // on n'a pas le droit de faire block ici
		return param.flatMap(msg -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(crypt.generateAllCesar(msg.getMessage()))));
	}
}
