package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    static EmKa create() throws Exception {
        return new KRaftee();
    }

    String bootstrapServers();

    EmKa start();

    EmKa stop();

    @Override
    default void close() {
        //noinspection resource
        stop();
    }
}
