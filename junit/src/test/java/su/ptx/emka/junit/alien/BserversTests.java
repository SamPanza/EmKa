package su.ptx.emka.junit.alien;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import su.ptx.emka.aux.Node;
import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.EmKa;

@EmKa
class BserversTests {
  @BootstrapServers
  private String bservers1;

  @Test
  void bservers(@BootstrapServers String bservers2) {
    assertTrue(bservers1.matches(Node.host + ":\\d{4,}"));
    assertEquals(bservers1, bservers2);
  }
}
