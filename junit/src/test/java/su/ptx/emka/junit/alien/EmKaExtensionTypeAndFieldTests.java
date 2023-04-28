package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(EmKaExtension.class)
class EmKaExtensionTypeAndFieldTests {
    @BootstrapServers
    private String bServers;

    @Test
    void bServers_here() {
        //TODO: Make it fail
        assertNull(bServers);
    }
}
