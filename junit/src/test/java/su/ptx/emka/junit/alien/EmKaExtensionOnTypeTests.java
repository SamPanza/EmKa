package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;

@ExtendWith(EmKaExtension.class)
class EmKaExtensionOnTypeTests {
    @Test
    void test1() {
        System.err.println("test1");
    }

    @Test
    void test2() {
        System.err.println("test2");
    }
}
