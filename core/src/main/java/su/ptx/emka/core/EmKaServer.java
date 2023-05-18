package su.ptx.emka.core;

import java.io.File;

public interface EmKaServer extends AutoCloseable {
    static EmKaServer create() {
        return new KRaftee();
    }

    String bootstrapServers();

    default EmKaServer start() {
        return start(0, 0, null);
    }

    EmKaServer start(int port1, int port2, File dir);

    EmKaServer stop();

    @Override
    default void close() {
        //noinspection resource
        stop();
    }
}
