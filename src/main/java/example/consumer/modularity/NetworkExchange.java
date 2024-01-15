package example.consumer.modularity;

import example.consumer.Client;

public interface NetworkExchange {
    void getRequestHello();
    void setConsumer(ServiceConsumer consumer);

    void fin(Client client);
}
