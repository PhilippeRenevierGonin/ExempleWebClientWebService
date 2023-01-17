package example.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {

	/**
	 * Point d'entrée du programme
	 * Par scan du package, un bean (dans le composant de configuration EncryptedMessageRouter) et le RestController (le composant EncryptedMessageController)
	 * les deux font la même chose, l'un avec les chemins /hello ; /cesear/ ; /allcesarpost et /allcesarpostobj (pour le Router)
	 * et l'autre /hello2 ; /cesear2/ ; /allcesar2post et /allcesar2postobj (pour le RestController)
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}

}
