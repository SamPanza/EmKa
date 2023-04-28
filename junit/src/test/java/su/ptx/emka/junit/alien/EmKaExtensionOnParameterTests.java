package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKaExtension;

@ExtendWith(EmKaExtension.class)
class EmKaExtensionOnParameterTests {
    @Test
    void test1(@BootstrapServers String bServers) {
        System.err.println("test1:" + bServers);
    }

    @Test
    void test2() {
        System.err.println("test2");
    }
}
