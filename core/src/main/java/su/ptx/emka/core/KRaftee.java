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

    KRaftee() throws Exception {
        var props = newProps(newLogDir());
        bootstrapServers = props._1;
        krs = new KafkaRaftServer(new KafkaConfig(props._2, false), Time.SYSTEM, Option.empty());
    }

    @Override
    public String bootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public EmKa start() {
        krs.startup();
        return this;
    }

    @Override
    public EmKa stop() {
        krs.shutdown();
        krs.awaitShutdown();
        return this;
    }

    private static Tuple2<String, Map<?, ?>> newProps(File logDir) {
        IntSupplier port0 = () -> {
            try (var s = new ServerSocket(0)) {
                return s.getLocalPort();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        var ctl = "CTL";
        var cp = port0.getAsInt();
        var bro = "BRO";
        var bp = port0.getAsInt();
        short rf = 1;
        return new Tuple2<>(
                "%s:%d".formatted(HOST, bp),
                Map.of(
                        "process.roles", "controller,broker",
                        "node.id", NODE_ID,
                        "controller.quorum.voters", "%d@%s:%d".formatted(NODE_ID, HOST, cp),
                        "listeners", "%s://%s:%d".formatted(ctl, HOST, cp) + "," + "%s://%s:%d".formatted(bro, HOST, bp),
                        "listener.security.protocol.map", "%s:PLAINTEXT,%s:PLAINTEXT".formatted(ctl, bro),
                        "controller.listener.names", ctl,
                        "inter.broker.listener.name", bro,
                        "log.dir", logDir.toString(),
                        "offsets.topic.replication.factor", rf,
                        "transaction.state.log.replication.factor", rf));
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
