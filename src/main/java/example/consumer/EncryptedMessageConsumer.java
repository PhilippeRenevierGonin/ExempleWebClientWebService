package example.consumer;

import example.data.Message;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class EncryptedMessageConsumer {

	private final WebClient client;


	public EncryptedMessageConsumer(WebClient.Builder builder) {
		this.client = builder.baseUrl("http://localhost:8080").build();
	}

	public Mono<String> getMessage() {
		return this.client.get().uri("/hello").accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Message.class)
				.map(Message::getMessage);
	}


	public Flux<Message> getAllCesar(String s) {
		return this.client.get().uri("/allcesar2/", uri -> uri.queryParam("val", s).build()).accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(Message.class);
	}

	public Flux<Message> getAllCesarPost(String s) throws URISyntaxException {
		return this.client.post().uri(new URI("http://localhost:8080/allcesar2post/"))
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(s), String.class).retrieve()
				.bodyToFlux(Message.class);
	}


	public Flux<Message> getAllCesarPostObj(Message s) throws URISyntaxException {
		return this.client.post().uri(new URI("http://localhost:8080/allcesar2postobj/"))
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(s), Message.class).retrieve()
				.bodyToFlux(Message.class);
	}


	public void fin() {
		this.client.get().uri("/fin2")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Void.class).block();
	}

	public Flux<Message> getAllCesarPostObjFunc(Message m) throws URISyntaxException {
		return this.client.post().uri(new URI("http://localhost:8080/allcesarpost/"))
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(m), Message.class).retrieve()
				.bodyToFlux(Message.class);
	}
}
