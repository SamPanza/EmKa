package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    static EmKa create() {
        return new KRaftee();
    }

    String bootstrapServers();

    //NB: Throw custom rt exc?
    EmKa start() throws Exception;

    EmKa stop();

    @Override
    default void close() {
        stop();
    }
}
