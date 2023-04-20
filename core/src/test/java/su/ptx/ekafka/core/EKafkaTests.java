package su.ptx.ekafka.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EKafkaTests {
    @Test
    void itStartsAndStops() {
        var ek = EKafka.start();
        assertTrue(ek.running());
        ek.close();
        assertFalse(ek.running());
    }
}
