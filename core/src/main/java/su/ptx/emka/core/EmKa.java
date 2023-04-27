package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    String bootstrapServers();

    void stop();

    @Override
    default void close() {
        stop();
    }

    static EmKa start(short nBrokers) throws Exception {
        return KRaftee.start(nBrokers);
    }
}
