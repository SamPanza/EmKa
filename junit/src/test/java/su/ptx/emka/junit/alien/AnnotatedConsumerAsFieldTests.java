package su.ptx.emka.junit.alien;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Test;
import su.ptx.emka.junit.EmKa;
import su.ptx.emka.junit.Konsumer;

@EmKa
class AnnotatedConsumerAsFieldTests {
  private static final String GROUP = "1";

  @Konsumer(group = GROUP)
  private Consumer<UUID, String> consumer;

  @Test
  void consumerInjected() {
    assertEquals(GROUP, consumer.groupMetadata().groupId());
  }
}
