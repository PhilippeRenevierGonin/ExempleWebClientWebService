package example.consumer;

import example.data.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SpringBootApplication
public class Client {

	public static void main(String[] args) {
		// SpringApplication.run(Client.class, args);
		new SpringApplicationBuilder(Client.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Bean
	public CommandLineRunner aGame(@Autowired EncryptedMessageConsumer client) {
		return args -> {
			System.out.println("************************** App **************************************");

			// pour faire la différence entre un lancement via les tests et un lancement par mvn exec:java@id

				String s = client.getMessage().block();
			System.out.println(s);
				for(int i = 0; i < 20; System.out.println(i++) );
			Mono<String> sDemandée = client.getMessage();
			sDemandée.subscribe(new Consumer<String>() {
						@Override
						public void accept(String s) {
							System.out.println("on a accepté " + s);
						}
					});

			Flux<Message> toutesSDemandées = client.getAllCesar("hello");


			toutesSDemandées.subscribe(new Subscriber<Message>() {
				@Override
				public void onSubscribe(Subscription subscription) {
					System.out.println("début de souscription");
					subscription.request(30); // nb element

				}

				@Override
				public void onNext(Message g) {
					System.out.println("on a reçu de la souscription "+g.getMessage());

				}

				@Override
				public void onError(Throwable throwable) {

				}

				@Override
				public void onComplete() {
					System.out.println("fin de souscription");

				}
			});



				for(int i = 100; i < 120; i++ ) {
					System.out.println(i);
					TimeUnit.NANOSECONDS.sleep(1);

				}


			TimeUnit.SECONDS.sleep(1);
			System.out.println("fini");

		};
	}
}
