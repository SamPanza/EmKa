package su.ptx.emka.core;

import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import org.apache.kafka.common.utils.Time;
import scala.Option;

import java.io.File;
import java.util.Map;

import static su.ptx.emka.core.FreePorts.FREE_PORTS;

final class KRaftee implements EmKaServer {
    private String bootstrapServers;
    private KafkaRaftServer server;

    @Override
    public String bootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public synchronized EmKaServer start(int brokerPort, int controllerPort, File dir) {
        if (server != null) {
            throw new IllegalStateException("Server already started");
        }
        brokerPort = FREE_PORTS.applyAsInt(brokerPort);
        controllerPort = FREE_PORTS.applyAsInt(controllerPort);
        var nodeId = 1;
        server = new KafkaRaftServer(
                new KafkaConfig(
                        Map.of(
                                "process.roles", "controller,broker",
                                "node.id", nodeId,
                                "controller.quorum.voters", nodeId + "@localhost:" + controllerPort,
                                "listeners", "BRO://localhost:%d,CON://localhost:%d".formatted(brokerPort, controllerPort),
                                "listener.security.protocol.map", "CON:PLAINTEXT,BRO:PLAINTEXT",
                                "controller.listener.names", "CON",
                                "inter.broker.listener.name", "BRO",
                                "log.dir", new LogDir(dir).format(nodeId).getAbsolutePath(),
                                "offsets.topic.replication.factor", (short) 1,
                                "transaction.state.log.replication.factor", (short) 1),
                        false),
                Time.SYSTEM,
                Option.empty());
        server.startup();
        bootstrapServers = "localhost:" + brokerPort;
        return this;
    }

    @Override
    public synchronized EmKaServer stop() {
        if (server != null) {
            bootstrapServers = null;
            server.shutdown();
            server.awaitShutdown();
            server = null;
        }
        return this;
    }
}
