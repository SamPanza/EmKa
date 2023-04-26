package su.ptx.ekafka.core;

public interface EKafka extends AutoCloseable {
    String bootstrapServers();

    void stop();

    @Override
    default void close() {
        stop();
    }

    static EKafka start() throws Exception {
        return EmbeddedKafka.start();
    }
}
