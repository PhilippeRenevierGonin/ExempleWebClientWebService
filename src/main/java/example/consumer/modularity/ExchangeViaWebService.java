package example.consumer.modularity;

import example.consumer.Client;
import example.consumer.RequestMaker;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component public class ExchangeViaWebService implements NetworkExchange {
    private final RequestMaker requestMaker;
    private ServiceConsumer consumer;


    public ExchangeViaWebService(RequestMaker requestMaker) {
        this.requestMaker = requestMaker;
    }

    public void getRequestHello() {
        Mono<String> sDemandee = requestMaker.getMessage("/hello");
        System.out.println("Listener dans le tryptique MainCode / Echange / RequestMaker");
        sDemandee.subscribe(new java.util.function.Consumer<String>() {
            @Override
            public void accept(String s) {
                consumer.newAnswer(s);
            }
        });
    }

    @Override
    public void setConsumer(ServiceConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void fin(Client client) {
        client.fin(requestMaker);
    }
}
