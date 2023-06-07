package su.ptx.emka.clients.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

class KadmTests {
  @Test
  void createWithInvalidBservers() {
    var e1 = assertThrows(KafkaException.class, () -> {
      try (var ignored = new Kadm("")) {
        fail("Shouldn't be here");
      }
    });
    assertEquals("Failed to create new KafkaAdminClient", e1.getMessage());
    var e2 = e1.getCause();
    assertTrue(e2 instanceof ConfigException);
    assertEquals("No resolvable bootstrap urls given in bootstrap.servers", e2.getMessage());
  }
}
