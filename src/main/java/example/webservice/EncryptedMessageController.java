package example.webservice;

import example.data.Message;
import example.webservice.features.Cesar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * le restcontroller, avec 5 mapping, pour les 5 chemins exposés
 */
@CrossOrigin
@RestController
public class EncryptedMessageController {
    @Autowired
    Cesar cesar;

    @GetMapping("/hello2")
    public Message hello(){
        Message g = new Message("Hello, Spring!");
        return g;
    }

    @GetMapping("/cesar2/{key:[0-9]|1[0-9]|2[0-6]}")
    public Message cesar(@RequestParam(required = false) String val, @PathVariable("key") int key) {
        System.out.println("@GetMapping(\"/cesar2/{key:[0-9]|1[0-9]|2[0-6]}\"");
        String toCode = "no string";
        if ((val != null) && (! val.equals(""))) {
            toCode = val.toLowerCase();
        }
        return new Message(cesar.encodeCesar(toCode, key));
    }




    @GetMapping("/allcesar2")
    public ArrayList<Message> allCesar(@RequestParam(required = false) String val) {
        String toCode = "no string";
        if ((val != null) && (! val.equals(""))) {
            toCode = val.toLowerCase();
        }
        return cesar.generateAllCesar(toCode);
    }

    @PostMapping("/allcesar2post")
    public ArrayList<Message> allCesarpost(@RequestBody String val) {
        String toCode = "no string";
        if ((val != null) && (! val.equals(""))) {
            toCode = val.trim().toLowerCase();
        }
        return cesar.generateAllCesar(toCode);
    }

    @PostMapping("/allcesar2postobj")
    public ArrayList<Message> allCesarpostobj(@RequestBody Message val) {
        String toCode = "no string";
        if ((val != null) && (! val.equals(""))) {
            toCode = val.getMessage().toLowerCase();
        }
        return cesar.generateAllCesar(toCode);
    }


    @GetMapping("/fin2")
    public void finir() {
        System.out.println("fin du serveur sur demande, pour travis...");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.exit(0);
            }
        });
        t.start();
    }


}
