package su.ptx.ekafka.core;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static su.ptx.ekafka.core.Maps.map;

class MapsTests {
    @Test
    void mapWorks() {
        assertEquals(
                "Map(foo -> 42)",
                String.valueOf(map(Map.of("foo", 42))));
    }
}
