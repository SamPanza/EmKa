package su.ptx.ekafka.core;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static su.ptx.ekafka.core.JavaToScala.immutableMap;

class JavaToScalaTests {
    @Test
    void immutableMapWorks() {
        assertEquals(
                "Map(foo -> 42)",
                String.valueOf(immutableMap(Map.of("foo", 42))));
    }
}
