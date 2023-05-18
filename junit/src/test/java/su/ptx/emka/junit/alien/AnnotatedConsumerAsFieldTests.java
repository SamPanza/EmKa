package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Test;
import su.ptx.emka.junit.EmKa;
import su.ptx.emka.junit.Konsumer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EmKa
public class AnnotatedConsumerAsFieldTests {
    private static final String GROUP = "1";

    @Konsumer(group = GROUP)
    private Consumer<UUID, String> consumer;

    @Test
    void consumerInjected() {
        assertEquals(GROUP, consumer.groupMetadata().groupId());
    }
}
