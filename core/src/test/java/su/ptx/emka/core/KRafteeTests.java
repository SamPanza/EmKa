package su.ptx.emka.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KRafteeTests {
    @Test
    void no_bootstrapServers_until_start() {
        try (var s = new KRaftee()) {
            assertNull(s.bootstrapServers());
        }
    }

    @Test
    void cant_start_start() throws Exception {
        try (var s = new KRaftee()) {
            s.start();
            assertThrows(IllegalStateException.class, s::start);
        }
    }

    @Test
    void can_start_stop_start() throws Exception {
        try (var s = new KRaftee()) {
            s.start();
            s.stop();
            s.start();
        }
    }
}
