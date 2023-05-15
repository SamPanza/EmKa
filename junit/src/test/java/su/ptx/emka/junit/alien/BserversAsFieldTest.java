package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EmKaExtension.class)
class BserversAsFieldTest {
    @BootstrapServers
    private String b_servers;

    @Test
    @DisplayName("IF EmKaExtension is on test class THEN b_servers field is OK")
    void test() {
        assertNotNull(b_servers);
        assertTrue(b_servers.matches("localhost:\\d{4,}"));
    }
}
