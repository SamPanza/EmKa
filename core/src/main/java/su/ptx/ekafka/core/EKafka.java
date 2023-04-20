package su.ptx.ekafka.core;

import io.github.embeddedkafka.EmbeddedK;
import io.github.embeddedkafka.EmbeddedKafka$;
import io.github.embeddedkafka.EmbeddedKafkaConfigImpl;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.apache.kafka.metadata.BrokerState.RUNNING;
import static su.ptx.ekafka.core.JavaToScala.immutableMap;

public final class EKafka implements AutoCloseable {
    private final EmbeddedK ek;

    private EKafka(EmbeddedK ek) {
        this.ek = ek;
    }

    public static EKafka start() {
        return start(0, 0, emptyMap(), emptyMap(), emptyMap());
    }

    public static EKafka start(int kafkaPort,
                               int zooKeeperPort,
                               Map<String, String> brokerConfig,
                               Map<String, String> producerConfig,
                               Map<String, String> consumerConfig) {
        return new EKafka(
                EmbeddedKafka$.MODULE$.start(
                        new EmbeddedKafkaConfigImpl(
                                kafkaPort,
                                zooKeeperPort,
                                immutableMap(brokerConfig),
                                immutableMap(producerConfig),
                                immutableMap(consumerConfig))));
    }

    public boolean running() {
        return ek.broker().brokerState() == RUNNING;
    }

    @Override
    public void close() {
        ek.stop(true);
    }
}
