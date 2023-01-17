package example.consumer.modularity;

public interface ServiceConsumer {
    void newAnswer(String s);
    void setNetworkExchange(NetworkExchange network);

    void consume();
}
