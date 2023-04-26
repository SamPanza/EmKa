package su.ptx.ekafka.core;

import io.github.embeddedkafka.EmbeddedK;
import io.github.embeddedkafka.EmbeddedKafka$;
import io.github.embeddedkafka.EmbeddedKafkaConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class EmbeddedKafka implements EKafka {
    private static final Logger LOGGER = LoggerFactory.getLogger("ekafka");
    private final EmbeddedK embeddedK;

    EmbeddedKafka(EmbeddedK embeddedK) {
        this.embeddedK = embeddedK;
    }

    @Override
    public String bootstrapServers() {
        return embeddedK.broker().advertisedListeners().headOption()
                .map(ep -> ep.host() + ":" + ep.port())
                .get();
    }

    @Override
    public void stop() {
        LOGGER.debug("Stopping");
        embeddedK.stop(true);
        LOGGER.debug("Stopped");
    }

    public static EKafka start(int kafkaPort, int zooKeeperPort, Map<String, String> brokerConfig) {
        return new EmbeddedKafka(
                EmbeddedKafka$.MODULE$.start(
                        new EmbeddedKafkaConfigImpl(
                                kafkaPort,
                                zooKeeperPort,
                                Maps.map(brokerConfig),
                                Maps.empty(),
                                Maps.empty())));
    }
}
