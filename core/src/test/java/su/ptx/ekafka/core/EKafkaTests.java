package su.ptx.ekafka.core;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EKafkaTests {
    @Test
    void itStartsAndStops() {
        try (var ek = EKafka.start()) {
            assertTrue(Pattern.compile("localhost:\\d\\d\\d\\d+").matcher(ek.bootstrapServers).matches());
        }
    }
}
