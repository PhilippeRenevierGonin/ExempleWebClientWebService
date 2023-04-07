package example.consumer;

import example.data.Message;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class RequestMaker {

	private final WebClient webClient;


	public RequestMaker(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("http://localhost:8080").build();
	}

	public Mono<String> getMessage(String url) {
		return this.webClient.get().uri(url).accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Message.class)
				.map(Message::getMessage);
	}


	public Flux<Message> getAllCesar(String url, String s) {
		return this.webClient.get().uri(url, uri -> uri.queryParam("val", s).build()).accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(Message.class);
	}

	public Flux<Message> getAllCesarPost(String url, String s) throws URISyntaxException {
		return this.webClient.post().uri(new URI("http://localhost:8080/"+url))
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(s), String.class) // ou alternativtement BodyInserters.fromValue(s)
				.retrieve()
				.bodyToFlux(Message.class);
	}


	public Flux<Message> getAllCesarPostObj(String url, Message s) throws URISyntaxException {
		return this.webClient.post().uri(new URI("http://localhost:8080/"+url))
				.accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(s)) // ou alternativtement Mono.just(s), Message.class
				.retrieve()
				.bodyToFlux(Message.class);
	}


	public void fin() {
		this.webClient.get().uri("/fin2")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Void.class).block();
	}

	public Mono<Message> getMessageKey(String messageSpecial, int i) {
		return this.webClient.get()
				.uri("/cesar3key",
						uri -> uri.queryParam("val",messageSpecial)
								.queryParam("key",i).build())
				.retrieve()
				.bodyToMono(Message.class);
	}


	public Mono<Message> getMessageKeyPost(String messageSpecial, Integer i) {
		return this.webClient.post().uri("/cesar3keypost").accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromFormData("val",messageSpecial).with("key",""+i))
				.retrieve()
				.bodyToMono(Message.class);
	}


}
