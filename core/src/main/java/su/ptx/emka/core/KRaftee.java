package su.ptx.emka.core;

import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.BrokerServer;
import kafka.server.KafkaBroker;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import kafka.server.MetaProperties;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;
import org.apache.kafka.metadata.bootstrap.BootstrapMetadata;
import org.apache.kafka.server.common.MetadataVersion;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntSupplier;

final class KRaftee implements EmKa {
    private static final int NODE_ID = 1;

    private static String q_voters(int port) {
        return NODE_ID + "@" + host_port(port);
    }

    private static final String CTL = "CTL";
    private static final String BRO = "BRO";

    private static String lstnr(String name, int port) {
        return name + "://" + host_port(port);
    }

    private static final String HOST = "localhost";

    private static String host_port(int port) {
        return HOST + ":" + port;
    }

    private final KafkaRaftServer kafka;
    private final KafkaBroker bro;

    KRaftee() throws Exception {
        kafka = new KafkaRaftServer(
                new KafkaConfig(
                        newProps(newLogDir()), false),
                Time.SYSTEM,
                Option.empty());
        //
        @SuppressWarnings("JavaReflectionMemberAccess")
        var broField = KafkaRaftServer.class.getDeclaredField("broker");
        broField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var broOption = (Option<BrokerServer>) broField.get(kafka);
        bro = broOption.get();
    }

    @Override
    public String bootstrapServers() {
        return host_port(bro.boundPort(new ListenerName(BRO)));
    }

    @Override
    public EmKa start() {
        kafka.startup();
        return this;
    }

    @Override
    public EmKa stop() {
        kafka.shutdown();
        kafka.awaitShutdown();
        return this;
    }

    private static Map<?, ?> newProps(File logDir) {
        IntSupplier port0 = () -> {
            try (var s = new ServerSocket(0)) {
                return s.getLocalPort();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        var cp = port0.getAsInt();
        var bp = port0.getAsInt();
        short rf = 1;
        return Map.of(
                "process.roles", "controller,broker",
                "node.id", NODE_ID,
                "controller.quorum.voters", q_voters(cp),
                "listeners", lstnr(CTL, cp) + "," + lstnr(BRO, bp),
                "listener.security.protocol.map", "%s:PLAINTEXT,%s:PLAINTEXT".formatted(CTL, BRO),
                "controller.listener.names", CTL,
                "inter.broker.listener.name", BRO,
                "log.dir", logDir.toString(),
                "offsets.topic.replication.factor", rf,
                "transaction.state.log.replication.factor", rf);
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
