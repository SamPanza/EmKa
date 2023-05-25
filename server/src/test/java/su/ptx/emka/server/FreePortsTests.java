package su.ptx.emka.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static su.ptx.emka.server.FreePorts.FREE_PORTS;

import org.junit.jupiter.api.Test;

class FreePortsTests {
  @Test
  void random() {
    assertTrue(FREE_PORTS.applyAsInt(0) > 0);
  }

  @Test
  void not_random() {
    assertEquals(1, FREE_PORTS.applyAsInt(1));
  }
}
