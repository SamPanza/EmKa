package su.ptx.emka.core.alien;

import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKaServer;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmKaServerTests {
    private static void withCreatedEmKaServer(Consumer<EmKaServer> eksc) {
        try (var eks = EmKaServer.create()) {
            eksc.accept(eks);
        }
    }

    private static void withRunningEmKaServer(Consumer<EmKaServer> eksc) {
        withCreatedEmKaServer(eks -> eksc.accept(eks.run()));
    }

    @Test
    void NO_bootstrapServers_until_run() {
        withCreatedEmKaServer(eks -> assertNull(eks.bootstrapServers()));
    }

    @Test
    void CANNOT_run_run() {
        withRunningEmKaServer(eks -> assertThrows(IllegalStateException.class, eks::run));
    }

    @Test
    void can_run_close_run() {
        withRunningEmKaServer(eks -> {
            eks.close();
            eks.run();
        });
    }
}
