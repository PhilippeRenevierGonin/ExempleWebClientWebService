package example.webservice;

import example.data.Message;
import example.webservice.features.Cesar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

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

}
