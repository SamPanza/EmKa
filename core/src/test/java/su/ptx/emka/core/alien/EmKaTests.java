package su.ptx.emka.core.alien;

import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKa;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmKaTests {
    private static void withNewEmKa(Consumer<EmKa> ekc) {
        try (var ek = EmKa.create()) {
            ekc.accept(ek);
        }
    }

    private static void withStartedEmKa(Consumer<EmKa> ekc) {
        withNewEmKa(ek -> {
            try {
                ekc.accept(ek.start());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void no_bootstrapServers_until_start() {
        withNewEmKa(kr -> assertNull(kr.bootstrapServers()));
    }

    @Test
    void CANNOT_start_start() {
        withStartedEmKa(ek -> assertThrows(IllegalStateException.class, ek::start));
    }

    @Test
    void can_start_stop_start() {
        withStartedEmKa(ek -> {
            try {
                ek.stop().start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
