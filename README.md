# ExempleWebClientWebService

<p style="background-color: #ffc0cb; font-weight: bold;">
Attention : il y a des boucles for pour laisser les thread de WebFlux faire les requêtes, c'est artisanal : cela peut varier d'une machine à l'autre, et si le programme n'a pas assez de temps pour effectuer et traiter les réponses aux requêtes, il y aura des exceptions.
</p>

```
mvn clean package
Le serveur : mvn exec:java@server ou  mvn spring-boot:run@server
Une application qui utilise le webservice : 
mvn exec:java@client -Dexec.cleanupDaemonThreads=false 
ou mvn spring-boot:run@client
```

## Que fait le client ? 

Il y a 3 Beans exécutés : 
1. callWithParameters : il fait 2 requêtes synchrones (block) pour illustrer le passage de plusieurs paramètres (facile en GET, plus compliqué côté serveur en POST)
2. threeClassesSeparation : il fait quelques requêtes, mais c'est surtout pour le découpage en 3 parties : le code "métier" (MainCode qui implémente ServiceConsumer), le code d'encapsulation des échanges "réseaux" (ExchangeViaWebService qui implémente NetworkExchange) et la partie qui fait les requêtes : RequestMacker. Cela montre aussi la différence entre synchrone et asynchrone, et sur quel thread c'est exécuté.
3. severalRequests : qui fait différentes requêtes, en double (sur un RestController et sur une paire Router / Handler) pour montrer que c'est identique. Il y a des requêtes GET, avec ou sans paramètre, avec des paramètres de types "trivial" (entier, String) ou des objets, et cela montre aussi la différence entre synchrone et asynchrone, et sur quel thread c'est exécuté.

### callWithParameters

il fait juste 2 requêtes, équivalentes, mais une en get, une autre en post, avec 2 paramètres. 

### threeClassesSeparation

il y a donc la séparation en 3 parties, pour rendre la partie "qui consomme" les services extérieurs indépendent de l'implémentation des échanges (et des services). Il y a juste une requête asynchrone (il faut laisser du temps à celles-ci de se faire). 
Les dépendences sont exprimées en fonction des interfaces, pas des types réels. 
ExchangeViaWebService fait aussi de tampon entre la demande de service (MainCode) et ce qui fait effectivement la requête (RequestMaker). Il y a un choix de conception à faire ici pour déterminer si l'interface NetworkExchange contient du métier (comme ici) ou fait les choses génériquement. 

### severalRequests

Les requêtes faites dans l'ordre sont : 
1. 2 requêtes synchrones (block) sur hello dans les deux modes RestController (RC) / Router+Handler (RH). La boucle for qui suit illustre que c'est bien séquentiel.
2. 2 appels (RC et RH) de 2 requêtes avec un seul résultat en GET (via requeteMonoGet) et sans paramètre. Les requêtes sont asynchrones, il y a un abonnement via un Consumer. 
3. 2 appels (RC et RH) de 2 requêtes avec plusieurs résultats en GET (via requeteFluxGet) avec un paramètre de type String. Les requêtes sont asynchrones, il y a un abonnement via un Subscriber.
4. 2 appels (RC et RH) de 2 requêtes avec plusieurs résultats en POST (via requeteFluxPost) avec un paramètre de type String (avec une alternative pour passer le paramètre dans RequestMaker), il y a un abonnement via un Subscriber.
5. 2 appels (RC et RH) de 2 requêtes avec plusieurs résultats en POST (via requeteFluxPost) avec un paramètre de type Object (ici Message), il y a un abonnement via un Subscriber.
6. il y a la boucle for d'attente pour simuler un traitement en parallèle des requêtes
7. à la fin, on déclenche la fin de tout (on envoie un message au serveur pour qu'il se ferme), ceci est pour 'assurer' que tout s'arrête (y compris pour une exécution dans le cloud dans travis)


## Que fait le serveur ? 

Il propose 2 fois les mêmes routes : 
 * les chemins indiqués en premiers (sans 2) sont traités par une paire Router (EncryptedMessageRouter) / Handler (EncryptedMessageHandler)
 * les chemins avec un 2 sont traités par un RestController, EncryptedMessageController 
 * "/hello" , "/hello2" --> juste pour dire bonjour, le retour est un Message de type "hello world"
 * "/cesar/{key:[0-9]|1[0-9]|2[0-6]}" et "/cesar2/{key:[0-9]|1[0-9]|2[0-6]}" --> un chemin variable, la partie variable indique le codage (1-26) et en paramètre une String à coder, en retour on a la String codée encapsulée dans un Message
 * "/allcesar" et "/allcesar2" : pour retourner (GET) tous les encodages "caesar" sous forme d'une liste de Message. Le "mot" à coder est passer en paramètre sous forme de String
 * "/allcesarpost" et /allcesar2post" : pour retourner (POST) tous les encodages "caesar" sous forme d'une liste de Message. Le "mot" à coder est passer en paramètre sous forme de String
 * "/allcesarpostobj" et "/allcesar2postobj" : pour retourner (POST) tous les encodages "caesar" sous forme d'une liste de Message. Le "mot" à coder est passer en paramètre sous forme d'Objet de type Message

Il y a en plus dans le RestController le chemin "/fin2" pour arrêter le serveur (c'est juste pour l'exemple). 

Il y a aussi "/cesar3key" (dans un autre RestController) et "/cesar3keypost" (dans une autre paire Router/Handler) pour illustrer la réception de plusieurs paramètres.
Si "/cesar3key" aurait pu être fait en mode "Router/Handler", "/cesar3keypost" n'aurait pas pu être fait en RestController (qui ne peut recevoir qu'un paramètre). 