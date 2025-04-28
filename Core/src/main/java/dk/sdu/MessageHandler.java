package dk.sdu;

@FunctionalInterface
public interface MessageHandler {
    void handle(String topic, String message) throws Exception;
}
