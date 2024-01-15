package example.consumer;

import example.consumer.modularity.ServiceConsumer;
import example.consumer.modularity.NetworkExchange;
import example.data.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SpringBootApplication
public class Client {

	private int nbBeansFinis = 0;

	public static void main(String[] args) {
		// SpringApplication.run(Client.class, args);
		new SpringApplicationBuilder(Client.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}


	/**
	 * Pour illuster le passage de paramètres, le code n'est pas séparé, c'est fortement couplé à la technologie utilisée. Les requêtes sont synchrones.
	 * @param requestMaker : pour faire effectivement les requetes.
	 * @return  le bean d'exécution
	 */
	@Bean
	public CommandLineRunner callWithParameters(@Autowired RequestMaker requestMaker) {
		return args -> {
			StringBuilder s = new StringBuilder("********* requetes avec param *********\n");
			s.append("\n"+requestMaker.getMessageKey("message special", 4).block().getMessage());
			s.append("\n"+requestMaker.getMessageKeyPost("abcde fghij", 5).block().getMessage());
			s.append("\n********* fin requetes avec param *********\n");
			System.out.println(s);

			fin(requestMaker);
		};
	}



	/**
	 * exemple avec la séparation en trois parties
	 * @param principal : la partie qui déclenche l'appel au webservice, qui en a besoin
	 * @param requetes : l'encapsulation des requetes, pour les masquer à "principal". Cette partie a une dépendance à RequestMaker
	 * @return le bean d'exécution
	 */
	@Bean
	public CommandLineRunner threeClassesSeparation(@Autowired ServiceConsumer principal, @Autowired NetworkExchange requetes) {
		return args -> {
			principal.setNetworkExchange(requetes);
			principal.consume();

			requetes.fin(this);
		};
	}


	/**
	 * pour montrer le block (au début), puis fait des requêtes get, puis post, avec pas de paramètre, des paramètres String, puis des paramètres objet
	 * puis aussi avec des Flux. Le code n'est pas séparé, c'est fortement couplé à la technologie utilisée
	 * @param requestMaker : pour faire effectivement les requetes.
	 * @param builder : parametre inutile, juste pour montrer qu'on pourrait le récupérer ici
	 * @return le bean qui va faire plusieurs appels
	 */
	@Bean
	public CommandLineRunner severalRequests(@Autowired RequestMaker requestMaker, @Autowired WebClient.Builder builder) {
		return args -> {
			System.out.println("le builder ne sert pas ici, mais il aurait pu : "+builder);

			System.out.println("************************** requetes **************************************");
			System.out.println("************************** une bloquante / GET **************************************");
			
			String s = requestMaker.getMessage("/hello").block();
			s = s+"\n"+requestMaker.getMessage("/hello2").block();
			System.out.println(s+" sur le thread : "+Thread.currentThread().getName());
				for(int i = 0; i < 20; System.out.println(i++) );

			System.out.println("************************** une Mono / GET **************************************");
			requeteMonoGet(requestMaker, "/hello");
			requeteMonoGet(requestMaker, "/hello2");

			System.out.println("************************** une Flux / GET **************************************");

			requeteFluxGet(requestMaker, "/allcesar", "hello");
			requeteFluxGet(requestMaker, "/allcesar2", "bonjour");


			System.out.println("************************** une Flux / POST (string) **************************************");

			requeteFluxPost(requestMaker, "allcesarpost", "bye");
			requeteFluxPost(requestMaker, "allcesar2post", "eau");


			System.out.println("************************** une Flux / POST (obj) **************************************");

			requeteFluxPostUnObj(requestMaker, "allcesar2postobj", new Message("fini - webcontroller"));
			requeteFluxPostUnObj(requestMaker, "allcesar2postobj", new Message("fini - router / handler"));



			System.out.println("************************** une sortie pour montrer l'entrelacement **************************************");
			for(int i = 20; i < 100; i++ ) {
					System.out.println("thread principal " + i +" sur le thread : "+Thread.currentThread().getName());
					TimeUnit.NANOSECONDS.sleep(1);

				}

			fin(requestMaker);

		};
	}


	public synchronized void fin(RequestMaker requestMaker) {
		nbBeansFinis++;
		if (nbBeansFinis >= 3) {
			System.out.println("************************** petite tempo pour laisser le temps de finir **************************************");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            requestMaker.fin();
			System.out.println("-------------------------- fini --------------------------");
		}

	}


	private void requeteFluxPostUnObj(RequestMaker client, String url, Message message) throws URISyntaxException {
		Flux<Message> toutesSDemandees3 = client.getAllCesarPostObj(url, message);
		toutesSDemandees3.subscribe( new Subscriber<Message>() {
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


	private void requeteFluxPost(RequestMaker client, String url, String toBeCrypted) throws URISyntaxException {
		Flux<Message> toutesSDemandees2 = client.getAllCesarPost(url, toBeCrypted);
		toutesSDemandees2.subscribe( new Subscriber<Message>() {
			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println("début de souscription 2"+" sur le thread : "+Thread.currentThread().getName());
				// subscription.request(30); // nb element
				long count = toutesSDemandees2.count().block();
				System.out.println("début de souscription 2"+" sur le thread : "+Thread.currentThread().getName()+" count = "+count);
				subscription.request(count);
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

	private void requeteFluxGet(RequestMaker client, String url, String toBeCrypted) {
		Flux<Message> toutesSDemandees = client.getAllCesar(url, toBeCrypted);
		toutesSDemandees.subscribe( new Subscriber<Message>() {
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

	private void requeteMonoGet(RequestMaker client, String url) {
		Mono<String> sDemandee = client.getMessage(url);
		sDemandee.subscribe(new Consumer<String>() {
					@Override
					public void accept(String s) {
						System.out.println("on a accepté " + s+" sur le thread : "+Thread.currentThread().getName());
					}
				});
	}
}
