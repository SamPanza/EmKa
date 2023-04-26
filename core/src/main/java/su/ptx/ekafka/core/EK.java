package su.ptx.ekafka.core;

import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import kafka.server.MetaProperties;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;
import org.apache.kafka.metadata.bootstrap.BootstrapMetadata;
import org.apache.kafka.server.common.MetadataVersion;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public final class EK implements EKafka {
    private final String bootstrapServers;
    private final KafkaRaftServer krs;

    private EK(String bootstrapServers, KafkaRaftServer krs) {
        this.bootstrapServers = bootstrapServers;
        this.krs = krs;
    }

    @Override
    public String bootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public void stop() {
        krs.shutdown();
        krs.awaitShutdown();
    }

    public static EK start() throws Exception {
        var brokerPort = randomPort();
        var krs = new KafkaRaftServer(
                new KafkaConfig(newProps(newLogDir(), brokerPort, randomPort()), false),
                Time.SYSTEM,
                Option.empty());
        krs.startup();
        return new EK("localhost:" + brokerPort, krs);
    }

    //WHY: KafkaConfig accepts Map[_, _] which is Map<?, ?>
    private static Map<?, ?> newProps(File logDir, int brokerPort, int controllerPort) {
        final var NODE_ID = 1;
        return Map.of(
                "process.roles", "broker,controller",
                "node.id", NODE_ID,
                "controller.quorum.voters", NODE_ID + "@localhost:" + controllerPort,
                "listeners", format("BROKER://localhost:%d,CONTROLLER://localhost:%d", brokerPort, controllerPort),
                "listener.security.protocol.map", "BROKER:PLAINTEXT,CONTROLLER:PLAINTEXT",
                "controller.listener.names", "CONTROLLER",
                "inter.broker.listener.name", "BROKER",
                "log.dir", logDir.toString(),
                "offsets.topic.replication.factor", (short) 1,
                "transaction.state.log.replication.factor", (short) 1);
    }

    private static File newLogDir() throws Exception {
        final var SOURCE = "ekafka";
        final var META = "meta.properties";
        final var NODE_ID = 1;

        var logDir = Files.createTempDirectory(SOURCE).toFile();
        logDir.deleteOnExit();

        new BrokerMetadataCheckpoint(
                new File(
                        logDir,
                        META))
                .write(
                        new MetaProperties(
                                Uuid.randomUuid().toString(),
                                NODE_ID)
                                .toProperties());

        new BootstrapDirectory(
                logDir.toString(),
                Optional.empty())
                .writeBinaryFile(
                        BootstrapMetadata.fromVersion(
                                MetadataVersion.latest(),
                                SOURCE));

        return logDir;
    }

    private static int randomPort() throws IOException {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }
}
