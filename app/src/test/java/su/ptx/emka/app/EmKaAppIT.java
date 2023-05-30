package su.ptx.emka.app;

import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.objectName;
import static su.ptx.emka.app.Jmx.withConnection;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

//CHECKSTYLE-SUPPRESS: AbbreviationAsWordInName
class EmKaAppIT {
  @Test
  void inspect(TestReporter tr) {
    withConnection(9001, c -> {
      var n = objectName(EmKaApp.class);
      tr.publishEntry(
          Map.of(
              "bootstrap.servers", attr(c, n, "BootstrapServers"),
              "log.dir", attr(c, n, "LogDir")));
      //TODO: Connect client
    });
  }
}
