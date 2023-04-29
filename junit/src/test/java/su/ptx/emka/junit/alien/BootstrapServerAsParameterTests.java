package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EmKaExtension.class)
class BootstrapServerAsParameterTests {
    @Test
    void bServers_here(@BootstrapServers String bServers) {
        assertTrue(bServers.matches("localhost:\\d\\d\\d\\d+"));
    }
}
