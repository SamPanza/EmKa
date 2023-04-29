package su.ptx.emka.core;

import kafka.server.BrokerServer;
import kafka.server.KafkaBroker;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.utils.Time;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntSupplier;

final class KRaftee implements EmKa {
    private final KafkaRaftServer server;
    private final KafkaBroker bro;

    KRaftee() throws Exception {
        server = new KafkaRaftServer(Co.kafkaConfig(), Time.SYSTEM, Option.empty());
        @SuppressWarnings("JavaReflectionMemberAccess")
        var broField = KafkaRaftServer.class.getDeclaredField("broker");
        broField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var broOption = (Option<BrokerServer>) broField.get(server);
        bro = broOption.get();
    }

    @Override
    public String bootstrapServers() {
        return Co.bServers(bro::boundPort);
    }

    @Override
    public EmKa start() {
        server.startup();
        return this;
    }

    @Override
    public void stop() {
        server.shutdown();
        server.awaitShutdown();
    }

    private static final class Co {
        private static KafkaConfig kafkaConfig() throws Exception {
            return new KafkaConfig(serverProps(Node._1.formatTmpLogDir()), false);
        }

        private static Map<?, ?> serverProps(File logDir) {
            IntSupplier port_0 = () -> {
                try (var s = new ServerSocket(0)) {
                    return s.getLocalPort();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
            var cp = port_0.getAsInt();
            var bp = port_0.getAsInt();
            short rf = 1;
            return Map.of(
                    "process.roles", "controller,broker",
                    "node.id", Node._1.id,
                    "controller.quorum.voters", q_voters(cp),
                    "listeners", lstnr(CTL, cp) + "," + lstnr(BRO, bp),
                    "listener.security.protocol.map", "%s:PLAINTEXT,%s:PLAINTEXT".formatted(CTL, BRO),
                    "controller.listener.names", CTL,
                    "inter.broker.listener.name", BRO,
                    "log.dir", logDir.toString(),
                    "offsets.topic.replication.factor", rf,
                    "transaction.state.log.replication.factor", rf);
        }

        private static final int NODE_ID = 1;
        private static final String HOST = "localhost";
        private static final String CTL = "CTL";
        private static final String BRO = "BRO";

        private static String q_voters(int port) {
            return NODE_ID + "@" + host_port(port);
        }

        private static String lstnr(String name, int port) {
            return name + "://" + host_port(port);
        }

        private static String bServers(Function<ListenerName, Integer> f) {
            return host_port(f.apply(new ListenerName(BRO)));
        }

        private static String host_port(int port) {
            return HOST + ":" + port;
        }
    }
}
