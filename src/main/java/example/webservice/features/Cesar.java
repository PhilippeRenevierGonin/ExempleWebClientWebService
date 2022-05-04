package example.webservice.features;

import example.data.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Cesar {


    // a-z sont traités...
    public String encodeCesar(String toCode, int decalage) {
        if ((decalage < 0) || (decalage > 26)) return toCode;
        else {
            StringBuilder rep = new StringBuilder();
            int z = 'z';
            int a = 'a';
            for (int i=0;i<toCode.length();i++) {
                int ncCode = toCode.charAt(i) ;
                if ((ncCode >= a) && (ncCode <= z))
                {
                    ncCode += decalage;
                    if (ncCode > z) ncCode -= 26;
                }
                char nc = (char) ncCode;
                rep.append(nc);
            }
            return rep.toString();
        }
    }

    public ArrayList<Message> generateAllCesar(String toCode) {
        ArrayList<Message> result = new ArrayList<>();
        for(int i =0; i < 27; i++) {
            result.add(new Message(encodeCesar(toCode, i)));
        }
        return  result;
    }
}
