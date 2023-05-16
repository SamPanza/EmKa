package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EmKaExtension.class)
class BserversAsFieldAndParamTests {
    @BootstrapServers
    private String b_servers;

    @Test
    @DisplayName("IF EmKaExtension is on test class THEN b_servers field resolved")
    void testField() {
        assert_b_servers(b_servers);
    }

    @Test
    @DisplayName("IF EmKaExtension is on test class THEN b_servers param resolved")
    void testParam1(@BootstrapServers String b_servers) {
        assert_b_servers(b_servers);
    }

    @Test
    @ExtendWith(EmKaExtension.class)
    @DisplayName("Both class & method annotated - EmKa starts once")
    void testParam2(@BootstrapServers String b_servers) {
        assert_b_servers(b_servers);
    }

    private static void assert_b_servers(String b_servers) {
        assertTrue(b_servers.matches("localhost:\\d{4,}"));
    }
}