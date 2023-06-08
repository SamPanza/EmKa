package su.ptx.emka.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.withConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import su.ptx.emka.aux.Node;
import su.ptx.emka.clients.admin.Kadm;

class EmKaAppIT {
  @Test
  void inspect(TestReporter tr) {
    withConnection(9001, c -> {
      String bservers = attr(c, EmKaApp.class, "BootstrapServers");
      tr.publishEntry("bootstrap.servers", bservers);
      String ldir = attr(c, EmKaApp.class, "LogDir");
      tr.publishEntry("log.dir", ldir);
      try (var kadm = new Kadm(bservers)) {
        var qi = kadm.quorumInfo();
        assertEquals(Node.id, qi.leaderId());
        tr.publishEntry("leaderEpoch", String.valueOf(qi.leaderEpoch()));
        tr.publishEntry("highWatermark", String.valueOf(qi.highWatermark()));
        var voters = qi.voters();
        assertEquals(1, voters.size());
        assertEquals(Node.id, voters.get(0).replicaId());
        assertTrue(qi.observers().isEmpty());
        var ld = kadm.logDir();
        assertEquals(ldir, ld.getKey());
        tr.publishEntry("logDirDescription", ld.getValue().toString());
      }
    });
  }
}
