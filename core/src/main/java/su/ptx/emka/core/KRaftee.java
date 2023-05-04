package su.ptx.emka.core;

import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import kafka.server.MetaProperties;
import kafka.tools.StorageTool;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;
import org.apache.kafka.metadata.bootstrap.BootstrapMetadata;
import org.apache.kafka.server.common.MetadataVersion;
import scala.Option;
import scala.collection.immutable.Seq;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.Files.createTempDirectory;
import static su.ptx.emka.core.FreePorts.FREE_PORTS;

public final class KRaftee implements EmKa {
    private String bootstrapServers;
    private KafkaRaftServer server;

    @Override
    public String bootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public EmKa start() {
        return start(0, 0, null);
    }

    public synchronized EmKa start(int conp, int brop, File logd) {
        if (server != null) {
            throw new IllegalStateException("Server already started");
        }
        if (logd == null) {
            try {
                (logd = createTempDirectory(null).toFile()).deleteOnExit();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        var nodeId = 1;
        conp = FREE_PORTS.applyAsInt(conp);
        brop = FREE_PORTS.applyAsInt(brop);
        server = new KafkaRaftServer(
                new KafkaConfig(
                        Map.of(
                                "process.roles", "controller,broker",
                                "node.id", nodeId,
                                "controller.quorum.voters", nodeId + "@localhost:" + conp,
                                "listeners", "CON://localhost:%d,BRO://localhost:%d".formatted(conp, brop),
                                "listener.security.protocol.map", "CON:PLAINTEXT,BRO:PLAINTEXT",
                                "controller.listener.names", "CON",
                                "inter.broker.listener.name", "BRO",
                                "log.dir", formatLogDir(logd, nodeId).getAbsolutePath(),
                                "offsets.topic.replication.factor", (short) 1,
                                "transaction.state.log.replication.factor", (short) 1),
                        false),
                Time.SYSTEM,
                Option.empty());
        server.startup();
        bootstrapServers = "localhost:" + brop;
        return this;
    }

    @Override
    public synchronized EmKa stop() {
        if (server != null) {
            bootstrapServers = null;
            server.shutdown();
            server.awaitShutdown();
            server = null;
        }
        return this;
    }

    /**
     * See {@link StorageTool#formatCommand(PrintStream, Seq, MetaProperties, MetadataVersion, boolean)}
     */
    private static File formatLogDir(File logDir, int nodeId) {
        //TODO: Do nothing if dir already formatted
        new BrokerMetadataCheckpoint(
                new File(logDir, "meta.properties")).write(
                new MetaProperties(Uuid.randomUuid().toString(), nodeId).toProperties());
        var bd = new BootstrapDirectory(logDir.toString(), Optional.empty());
        var bm = BootstrapMetadata.fromVersion(MetadataVersion.latest(), "");
        try {
            bd.writeBinaryFile(bm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return logDir;
    }
}
