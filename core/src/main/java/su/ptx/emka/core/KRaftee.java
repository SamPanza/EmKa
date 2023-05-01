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
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

final class KRaftee implements EmKa {
    private String bootstrapServers;
    private KafkaRaftServer server;

    @Override
    public String bootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public EmKa start() throws Exception {
        var p0 = new FreePort();
        var cp = p0.getAsInt();
        var bp = p0.getAsInt();
        bootstrapServers = "localhost:" + bp;
        server = new KafkaRaftServer(
                new KafkaConfig(
                        Map.of(
                                "process.roles", "controller,broker",
                                "node.id", 1,
                                "controller.quorum.voters", "1@localhost:" + cp,
                                "listeners", "CTL://localhost:%d,BRO://localhost:%d".formatted(cp, bp),
                                "listener.security.protocol.map", "CTL:PLAINTEXT,BRO:PLAINTEXT",
                                "controller.listener.names", "CTL",
                                "inter.broker.listener.name", "BRO",
                                "log.dir", formatTmpLogDir().getAbsolutePath(),
                                "offsets.topic.replication.factor", (short) 1,
                                "transaction.state.log.replication.factor", (short) 1),
                        false),
                Time.SYSTEM,
                Option.empty());
        server.startup();
        return this;
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
            server.awaitShutdown();
            server = null;
        }
    }

    /**
     * See {@link StorageTool#formatCommand(PrintStream, Seq, MetaProperties, MetadataVersion, boolean)}
     */
    private static File formatTmpLogDir() throws Exception {
        var dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        new BrokerMetadataCheckpoint(new File(dir, "meta.properties")).write(
                new MetaProperties(Uuid.randomUuid().toString(), 1).toProperties());
        new BootstrapDirectory(
                dir.toString(),
                Optional.empty())
                .writeBinaryFile(BootstrapMetadata.fromVersion(MetadataVersion.latest(), ""));
        return dir;
    }
}
