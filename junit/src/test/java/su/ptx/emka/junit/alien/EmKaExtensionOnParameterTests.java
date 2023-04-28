package su.ptx.emka.junit.alien;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;

class EmKaExtensionOnParameterTests {
    @Test
    void test1(@ExtendWith(EmKaExtension.class) String foo) {
        System.err.println("test1:" + foo);
    }

    @Test
    void test2() {
        System.err.println("test2");
    }
}
