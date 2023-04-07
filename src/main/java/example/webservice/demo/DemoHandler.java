package example.webservice.demo;


import example.data.Message;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component public class DemoHandler {


    public Mono<ServerResponse> sayHello(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromValue(new Message("hello form demo router/handler")));
    }
}
