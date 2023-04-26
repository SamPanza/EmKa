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
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static su.ptx.ekafka.core.RandomPorts.randomPort;
import static su.ptx.ekafka.core.RandomPorts.randomPorts;

final class EmbeddedKafka implements EKafka {
    private final String bootstrapServers;
    private final KafkaRaftServer krs;

    private EmbeddedKafka(String bootstrapServers, KafkaRaftServer krs) {
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

    static EKafka start(short brokers) throws Exception {
        var controllerPort = randomPort();
        var brokerPorts = randomPorts(brokers);
        var krs = new KafkaRaftServer(
                new KafkaConfig(newProps(newLogDir(), controllerPort, brokerPorts), false),
                Time.SYSTEM,
                Option.empty());
        krs.startup();
        return new EmbeddedKafka(
                IntStream.of(brokerPorts).mapToObj(p -> "localhost:" + p).collect(joining(",")),
                krs);
    }

    private static final int NODE_ID = 1;

    private static Map<?, ?> newProps(File logDir, int controllerPort, int[] brokerPorts) {
        var rf = (short) brokerPorts.length;
        return Map.of(
                "process.roles", "controller,broker",
                "node.id", NODE_ID,
                "controller.quorum.voters", NODE_ID + "@localhost:" + controllerPort,
                //TODO
                "listeners", format("CONTROLLER://localhost:%d,BROKER://localhost:%d", controllerPort, brokerPorts[0]),
                "listener.security.protocol.map", "CONTROLLER:PLAINTEXT,BROKER:PLAINTEXT",
                "controller.listener.names", "CONTROLLER",
                "inter.broker.listener.name", "BROKER",
                "log.dir", logDir.toString(),
                "offsets.topic.replication.factor", rf,
                "transaction.state.log.replication.factor", rf);
    }

    private static File newLogDir() throws Exception {
        var logDir = Files.createTempDirectory("ekafka").toFile();
        logDir.deleteOnExit();

        new BrokerMetadataCheckpoint(
                new File(
                        logDir,
                        "meta.properties"))
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
                                "ekafka"));

        return logDir;
    }
}
