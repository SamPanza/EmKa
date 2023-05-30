package su.ptx.emka.server;

import static su.ptx.emka.server.FreePorts.FREE_PORTS;

import java.io.File;
import java.util.Map;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import org.apache.kafka.common.utils.Time;
import scala.Option;

final class KRaftee implements EmKaServer {
  private String bootstrapServers;
  private String logDir;
  private KafkaRaftServer server;

  @Override
  public synchronized String bootstrapServers() {
    return bootstrapServers;
  }

  @Override
  public synchronized String logDir() {
    return logDir;
  }

  @Override
  public synchronized EmKaServer run(final int port1, final int port2, final File dir) {
    if (server != null) {
      throw new IllegalStateException("Server already started");
    }
    var nodeId = 1;
    var broPort = FREE_PORTS.applyAsInt(port1);
    var conPort = FREE_PORTS.applyAsInt(port2);
    server = new KafkaRaftServer(
        new KafkaConfig(
            Map.of(
                "process.roles", "broker,controller",
                "node.id", nodeId,
                "listeners", "BRO://localhost:%d,CON://localhost:%d".formatted(broPort, conPort),
                "controller.quorum.voters", nodeId + "@localhost:" + conPort,
                "listener.security.protocol.map", "CON:PLAINTEXT,BRO:PLAINTEXT",
                "controller.listener.names", "CON",
                "inter.broker.listener.name", "BRO",
                "log.dir", logDir = new LogDirFormatter(dir).format(nodeId).getAbsolutePath(),
                "offsets.topic.replication.factor", (short) 1,
                "transaction.state.log.replication.factor", (short) 1),
            false),
        Time.SYSTEM,
        Option.empty());
    server.startup();
    bootstrapServers = "localhost:" + broPort;
    return this;
  }

  @Override
  public synchronized void close() {
    if (server != null) {
      logDir = null;
      bootstrapServers = null;
      server.shutdown();
      server.awaitShutdown();
      server = null;
    }
  }
}
