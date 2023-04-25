package su.ptx.ekafka.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EKafkaTests {
    private EKafka eKafka;

    @BeforeEach
    void setUp() {
        eKafka = EKafka.start(0, 0, emptyMap());
    }

    @AfterEach
    void tearDown() {
        eKafka.close();
    }

    @Test
    void bootstrapServers_looks_OK() {
        assertTrue(
                Pattern.compile("localhost:\\d\\d\\d\\d+")
                        .matcher(eKafka.bootstrapServers())
                        .matches());
    }
}
