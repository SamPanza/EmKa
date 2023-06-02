package su.ptx.emka.app;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.withConnection;

import org.apache.kafka.clients.admin.Admin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

class EmKaAppIT {
  @Test
  void inspect(TestReporter tr) {
    withConnection(9001, c -> {
      String bservers = attr(c, EmKaApp.class, "BootstrapServers");
      tr.publishEntry("bootstrap.servers", bservers);
      String ldir = attr(c, EmKaApp.class, "LogDir");
      tr.publishEntry("log.dir", ldir);
      try (var admin = Admin.create(singletonMap("bootstrap.servers", bservers))) {
        admin.describeMetadataQuorum().quorumInfo().whenComplete((qi, e) -> {
          assertNotNull(qi);
          assertNull(e);
          assertEquals(1, qi.leaderId());
          assertEquals(1, qi.leaderEpoch());
          tr.publishEntry("highWatermark", String.valueOf(qi.highWatermark()));
          var voters = qi.voters();
          assertEquals(1, voters.size());
          assertEquals(1, voters.get(0).replicaId());
          assertTrue(qi.observers().isEmpty());
        });
        admin.describeLogDirs(singleton(1)).allDescriptions().whenComplete((m, e) -> {
          assertNotNull(m);
          assertNull(e);
          var ldd1 = m.get(1);
          assertEquals(1, ldd1.size());
          assertEquals(ldir, ldd1.keySet().iterator().next());
        });
      }
    });
  }
}
