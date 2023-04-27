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
import scala.Tuple2;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

final class EmbeddedKafka implements EmKa {
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

    static EmKa start(short nBrokers) throws Exception {
        var props = newProps(newLogDir(), nBrokers);
        var krs = new KafkaRaftServer(new KafkaConfig(props._1, false), Time.SYSTEM, Option.empty());
        krs.startup();
        return new EmbeddedKafka(props._2, krs);
    }

    private static final int NODE_ID = 1;

    private static Tuple2<Map<?, ?>, String> newProps(File logDir, @SuppressWarnings("ParameterCanBeLocal") short nBrokers) {
        //TODO java.lang.IllegalArgumentException: requirement failed: Each listener must have a different name, listeners: ***
        nBrokers = 1;
        final var LOCALHOST_ = "localhost:";
        var fp = new FreePort();
        var cp = fp.next();
        var bps = IntStream.generate(fp).limit(nBrokers).toArray();
        return new Tuple2<>(
                Map.of(
                        "process.roles", "controller,broker",
                        "node.id", NODE_ID,
                        "controller.quorum.voters", NODE_ID + "@" + LOCALHOST_ + cp,
                        "listeners", "CONTROLLER://" + LOCALHOST_ + cp + "," + IntStream.of(bps).mapToObj(p -> "BROKER://" + LOCALHOST_ + p).collect(joining(",")),
                        "listener.security.protocol.map", "CONTROLLER:PLAINTEXT,BROKER:PLAINTEXT",
                        "controller.listener.names", "CONTROLLER",
                        "inter.broker.listener.name", "BROKER",
                        "log.dir", logDir.toString(),
                        "offsets.topic.replication.factor", nBrokers,
                        "transaction.state.log.replication.factor", nBrokers),
                IntStream.of(bps).mapToObj(p -> LOCALHOST_ + p).collect(joining(",")));
    }

    private static File newLogDir() throws Exception {
        var logDir = Files.createTempDirectory("ekafka").toFile();
        logDir.deleteOnExit();

        new BrokerMetadataCheckpoint(new File(logDir, "meta.properties")).write(
                new MetaProperties(Uuid.randomUuid().toString(), NODE_ID).toProperties());

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
