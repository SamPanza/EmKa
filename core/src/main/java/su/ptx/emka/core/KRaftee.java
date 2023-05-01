package su.ptx.emka.core;

import kafka.server.BrokerServer;
import kafka.server.KafkaBroker;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.utils.Time;
import scala.Option;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

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
            var cp = new FreePort().getAsInt();
            var bp = new FreePort().getAsInt();
            short rf = 1;
            return Map.ofEntries(
                    entry("process.roles", "controller,broker"),
                    entry("node.id", Node._1.id),
                    entry("controller.quorum.voters", q_voters(cp)),
                    entry("listeners", lstnr(CTL, cp) + "," + lstnr(BRO, bp)),
                    entry("listener.security.protocol.map", "%s:PLAINTEXT,%s:PLAINTEXT".formatted(CTL, BRO)),
                    entry("controller.listener.names", CTL),
                    entry("inter.broker.listener.name", BRO),
                    entry("log.dir", logDir.toString()),
                    entry("offsets.topic.replication.factor", rf),
                    entry("transaction.state.log.replication.factor", rf));
        }

        private static final String CTL = "CTL";
        private static final String BRO = "BRO";

        private static final Host H = new Host("localhost");

        private static String q_voters(int port) {
            return Node._1 + "@" + H.withPort(port);
        }

        private static String lstnr(String name, int port) {
            return name + "://" + H.withPort(port);
        }

        private static String bServers(Function<ListenerName, Integer> f) {
            return H.withPort(f.apply(new ListenerName(BRO))).toString();
        }
    }
}
