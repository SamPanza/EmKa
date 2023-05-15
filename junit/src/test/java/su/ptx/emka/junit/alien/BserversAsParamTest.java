package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BserversAsParamTest {
    @Test
    @ExtendWith(EmKaExtension.class)
    @DisplayName("IF EmKaExtension is on test method THEN b_servers param resolved")
    void test(@BootstrapServers String b_servers) {
        assertNotNull(b_servers);
        assertTrue(b_servers.matches("localhost:\\d{4,}"));
    }
}
