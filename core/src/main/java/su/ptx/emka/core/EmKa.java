package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    int NODE_ID = 1;
    String HOST = "localhost";

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
