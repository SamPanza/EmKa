package su.ptx.ekafka.core;

public interface EmKa extends AutoCloseable {
    String bootstrapServers();

    void stop();

    @Override
    default void close() {
        stop();
    }

    static EmKa start(short nBrokers) throws Exception {
        return EmbeddedKafka.start(nBrokers);
    }
}
