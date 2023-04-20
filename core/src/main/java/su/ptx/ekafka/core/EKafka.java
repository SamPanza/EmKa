package su.ptx.ekafka.core;

import io.github.embeddedkafka.EmbeddedK;
import io.github.embeddedkafka.EmbeddedKafka$;
import io.github.embeddedkafka.EmbeddedKafkaConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static su.ptx.ekafka.core.JavaToScala.immutableMap;

public final class EKafka implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(EKafka.class);
    private final EmbeddedK ek;
    public final String bootstrapServers;

    private EKafka(EmbeddedK embeddedK) {
        ek = embeddedK;
        var endpoint = ek.broker().advertisedListeners().head();
        bootstrapServers = endpoint.host() + ":" + endpoint.port();
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

    @Override
    public void close() {
        LOGGER.debug("Stopping");
        ek.stop(true);
        LOGGER.debug("Stopped");
    }
}
