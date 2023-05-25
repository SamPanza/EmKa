package su.ptx.emka.core.alien;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKaServer;

class EmKaServerTests {
  private static void withCreatedEmKaServer(Consumer<EmKaServer> eksc) {
    try (var eks = EmKaServer.create()) {
      eksc.accept(eks);
    }
  }

  private static void withRunningEmKaServer(Consumer<EmKaServer> eksc) {
    withCreatedEmKaServer(eks -> eksc.accept(eks.run()));
  }

  @Test
  void no_bootstrapServers_until_run() {
    withCreatedEmKaServer(eks -> assertNull(eks.bootstrapServers()));
  }

  @Test
  void cant_run_run() {
    withRunningEmKaServer(eks -> assertThrows(IllegalStateException.class, eks::run));
  }

  @Test
  void can_run_close_run() {
    withRunningEmKaServer(eks -> {
      eks.close();
      eks.run();
    });
  }
}
