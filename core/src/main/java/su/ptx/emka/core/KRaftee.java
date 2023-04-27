package su.ptx.emka.core;

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
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntSupplier;

final class KRaftee implements EmKa {
    private final String bootstrapServers;
    private final KafkaRaftServer krs;

    private KRaftee(String bootstrapServers, KafkaRaftServer krs) {
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

    static EmKa start() throws Exception {
        var props = newProps(newLogDir());
        var krs = new KafkaRaftServer(new KafkaConfig(props._1, false), Time.SYSTEM, Option.empty());
        krs.startup();
        return new KRaftee(props._2, krs);
    }

    private static final int NODE_ID = 1;

    private static Tuple2<Map<?, ?>, String> newProps(File logDir) {
        IntSupplier port0 = () -> {
            try (var s = new ServerSocket(0)) {
                return s.getLocalPort();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        var cp = port0.getAsInt();
        var bp = port0.getAsInt();
        return new Tuple2<>(
                Map.of(
                        "process.roles", "controller,broker",
                        "node.id", NODE_ID,
                        "controller.quorum.voters", NODE_ID + "@localhost:" + cp,
                        "listeners", "CTL://localhost:" + cp + ",BRO://localhost:" + bp,
                        "listener.security.protocol.map", "CTL:PLAINTEXT,BRO:PLAINTEXT",
                        "controller.listener.names", "CTL",
                        "inter.broker.listener.name", "BRO",
                        "log.dir", logDir.toString(),
                        "offsets.topic.replication.factor", "1",
                        "transaction.state.log.replication.factor", "1"),
                "localhost:" + bp);
    }

    private static File newLogDir() throws Exception {
        var logDir = Files.createTempDirectory("emka").toFile();
        logDir.deleteOnExit();

        new BrokerMetadataCheckpoint(new File(logDir, "meta.properties")).write(
                new MetaProperties(Uuid.randomUuid().toString(), NODE_ID).toProperties());

        new BootstrapDirectory(
                logDir.toString(),
                Optional.empty())
                .writeBinaryFile(
                        BootstrapMetadata.fromVersion(
                                MetadataVersion.latest(),
                                "emka"));

        return logDir;
    }
}
