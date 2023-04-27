package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    String bootstrapServers();

    void stop();

    @Override
    default void close() {
        stop();
    }

    static EmKa start() throws Exception {
        return KRaftee.start();
    }
}
