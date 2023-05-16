package su.ptx.emka.core.alien;

import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKaServer;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmKaTests {
    private static void withNewEmKa(Consumer<EmKaServer> ekc) {
        try (var ek = EmKaServer.create()) {
            ekc.accept(ek);
        }
    }

    private static void withStartedEmKa(Consumer<EmKaServer> ekc) {
        withNewEmKa(ek -> ekc.accept(ek.start()));
    }

    @Test
    void NO_bootstrapServers_until_start() {
        withNewEmKa(kr -> assertNull(kr.bootstrapServers()));
    }

    @Test
    void CANNOT_start_start() {
        withStartedEmKa(ek -> assertThrows(IllegalStateException.class, ek::start));
    }

    @Test
    void can_start_stop_start() {
        withStartedEmKa(ek -> ek.stop().start());
    }
}
