package example.consumer;

import example.data.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
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
	public CommandLineRunner plusieursRequetes(@Autowired EncryptedMessageConsumer client) {
		return args -> {
			System.out.println("************************** requetes **************************************");
			System.out.println("************************** une bloquante / GET **************************************");
			
			String s = client.getMessage().block();
			System.out.println(s+" sur le thread : "+Thread.currentThread().getName());
				for(int i = 0; i < 20; System.out.println(i++) );

			System.out.println("************************** une Mono / GET **************************************");
			requeteMonoGet(client);

			System.out.println("************************** une Flux / GET **************************************");

			requeteFluxGet(client);


			System.out.println("************************** une Flux / POST (string) **************************************");

			requeteFluxPost(client);


			System.out.println("************************** une Flux / POST (obj) **************************************");

			requeteFluxPostUnObj(client);


			System.out.println("************************** une sortie pour montrer l'entrelacement **************************************");
			for(int i = 100; i < 120; i++ ) {
					System.out.println("thread principal " + i +" sur le thread : "+Thread.currentThread().getName());
					TimeUnit.NANOSECONDS.sleep(1);

				}


			System.out.println("************************** petite tempo pour laisser le temps de finir **************************************");
			TimeUnit.SECONDS.sleep(2);
			client.fin();
			System.out.println("-------------------------- fini --------------------------");
		};
	}

	private void requeteFluxPostUnObj(EncryptedMessageConsumer client) throws URISyntaxException {
		Flux<Message> toutesSDemandées3 = client.getAllCesarPostObj(new Message("fini"));
		toutesSDemandées3.subscribe( new Subscriber<Message>() {
			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println("début de souscription 3"+" sur le thread : "+Thread.currentThread().getName());
				subscription.request(30); // nb element
			}

			@Override
			public void onNext(Message g) {
				System.out.println("on a reçu de la souscription 3 "+g.getMessage()+" sur le thread : "+Thread.currentThread().getName());

			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("début de onError 3"+" sur le thread : "+Thread.currentThread().getName());
				throwable.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.println("fin de souscription 3 "+" sur le thread : "+Thread.currentThread().getName());

			}
		});
	}


	private void requeteFluxPost(EncryptedMessageConsumer client) throws URISyntaxException {
		Flux<Message> toutesSDemandées2 = client.getAllCesarPost("bye");
		toutesSDemandées2.subscribe( new Subscriber<Message>() {
			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println("début de souscription 2"+" sur le thread : "+Thread.currentThread().getName());
				subscription.request(30); // nb element
			}

			@Override
			public void onNext(Message g) {
				System.out.println("on a reçu de la souscription 2 "+g.getMessage()+" sur le thread : "+Thread.currentThread().getName());

			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("début de onError 2"+" sur le thread : "+Thread.currentThread().getName());
				throwable.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.println("fin de souscription 2 "+" sur le thread : "+Thread.currentThread().getName());

			}
		});
	}

	private void requeteFluxGet(EncryptedMessageConsumer client) {
		Flux<Message> toutesSDemandées = client.getAllCesar("hello");
		toutesSDemandées.subscribe( new Subscriber<Message>() {
			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println("début de souscription 1"+" sur le thread : "+Thread.currentThread().getName());
				subscription.request(30); // nb element

			}

			@Override
			public void onNext(Message g) {
				System.out.println("on a reçu de la souscription 1 "+g.getMessage()+" sur le thread : "+Thread.currentThread().getName());

			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {
				System.out.println("fin de souscription 1"+" sur le thread : "+Thread.currentThread().getName());

			}
		});
	}

	private void requeteMonoGet(EncryptedMessageConsumer client) {
		Mono<String> sDemandée = client.getMessage();
		sDemandée.subscribe(new Consumer<String>() {
					@Override
					public void accept(String s) {
						System.out.println("on a accepté " + s+" sur le thread : "+Thread.currentThread().getName());
					}
				});
	}
}
