package su.ptx.emka.core;

public interface EmKaServer extends AutoCloseable {
    static EmKaServer create() {
        return new KRaftee();
    }

    String bootstrapServers();

    EmKaServer start();

    EmKaServer stop();

    @Override
    default void close() {
        //noinspection resource
        stop();
    }
}
