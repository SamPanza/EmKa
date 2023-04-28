package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;

class EmKaExtensionOnFieldTests {
    @ExtendWith(EmKaExtension.class)
    private String foo;

    @Test
    void test1() {
        System.err.println("test1:" + foo);
    }

    @Test
    void test2() {
        System.err.println("test2:" + foo);
    }
}
