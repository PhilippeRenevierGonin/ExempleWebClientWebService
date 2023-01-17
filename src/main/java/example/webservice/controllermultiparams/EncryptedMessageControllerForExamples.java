package example.webservice.controllermultiparams;

import example.data.Message;
import example.webservice.features.Cesar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class EncryptedMessageControllerForExamples {
    @Autowired
    Cesar cesar;

    @GetMapping("/cesar3key")
    public Message cesarKey(@RequestParam(required = false) String val, @RequestParam(required = false) int key) {
        String toCode = "no string";
        if ((val != null) && (! val.equals(""))) {
            toCode = val.toLowerCase();
        }
        return new Message(cesar.encodeCesar(toCode, key));
    }

}
