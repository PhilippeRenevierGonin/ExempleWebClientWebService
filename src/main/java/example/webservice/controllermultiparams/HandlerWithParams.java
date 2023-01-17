package example.webservice.controllermultiparams;

import example.data.Message;
import example.webservice.features.Cesar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component public class HandlerWithParams {
    @Autowired
    Cesar crypt;
    public Mono<ServerResponse> hello(ServerRequest serverRequest) {
        Mono<Object> answer = serverRequest.formData().map(data ->  {
            String toCode = data.getFirst("val");
            int key = Integer.parseInt(data.getFirst("key"));
            Message m = new Message(crypt.encodeCesar(toCode, key));
            return m;
        });
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(answer, Message.class);

    }
}
