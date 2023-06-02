package su.ptx.emka.app;

import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.withConnection;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

class EmKaAppIT {
  @Test
  void inspect(TestReporter tr) {
    withConnection(9001, c -> {
      tr.publishEntry(
          Map.of(
              "bootstrap.servers", attr(c, EmKaApp.class, "BootstrapServers"),
              "log.dir", attr(c, EmKaApp.class, "LogDir")));
      //TODO: Connect client
    });
  }
}
