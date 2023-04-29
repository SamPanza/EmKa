package su.ptx.emka.core;

public interface EmKa extends AutoCloseable {
    static EmKa create() throws Exception {
        return new KRaftee();
    }

    String bootstrapServers();

    EmKa start();

    @Override
    void close();
}
