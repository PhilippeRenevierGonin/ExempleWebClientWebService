package example.consumer.modularity;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MainCode implements ServiceConsumer {


    private NetworkExchange network;
    @Override
    public void setNetworkExchange(NetworkExchange network) {
        this.network = network;
        network.setConsumer(this);
    }

    @Override
    public void consume() {
        network.getRequestHello();
        System.out.println("MainCode sur le thread : "+Thread.currentThread().getName());
        for(int i = 0; i < 50; System.out.println(i++) ) {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        };
    }

    @Override
    public void newAnswer(String s) {
        System.out.println("on a accepté en bout de chaine dans MainCode " + s+" sur le thread : "+Thread.currentThread().getName());
    }
}
