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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KRaftModeTests {
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

    private KafkaRaftServer krs;

    @BeforeEach
    void setUp() throws Exception {
        krs = new KafkaRaftServer(
                new KafkaConfig(newProps(newLogDir()), false),
                Time.SYSTEM,
                Option.empty());
        krs.startup();
    }

    @AfterEach
    void tearDown() {
        krs.shutdown();
        krs.awaitShutdown();
    }

    @Test
    void krs_is_OK() {
        assertNotNull(krs);
    }

    private static int randomPort() throws IOException {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

    //WHY: KafkaConfig accepts Map[_, _] which is Map<?, ?>
    private static Map<?, ?> newProps(File logDir) throws IOException {
        final var NODE_ID = 1;
        final var BRO_PORT = randomPort();
        final var CON_PORT = randomPort();
        return Map.of(
                "process.roles", "broker,controller",
                "node.id", NODE_ID,
                "controller.quorum.voters", NODE_ID + "@localhost:" + CON_PORT,
                "listeners", format("BROKER://localhost:%d,CONTROLLER://localhost:%d", BRO_PORT, CON_PORT),
                "listener.security.protocol.map", "BROKER:PLAINTEXT,CONTROLLER:PLAINTEXT",
                "controller.listener.names", "CONTROLLER",
                "inter.broker.listener.name", "BROKER",
                "log.dir", logDir.toString());
    }
}
