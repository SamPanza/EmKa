package su.ptx.ekafka.core;

import io.github.embeddedkafka.EmbeddedK;
import io.github.embeddedkafka.EmbeddedKafka$;
import io.github.embeddedkafka.EmbeddedKafkaConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface EKafka extends AutoCloseable {
    String bootstrapServers();

    void stop();

    @Override
    default void close() {
        stop();
    }

    static EKafka start(int kafkaPort, int zooKeeperPort, Map<String, String> brokerConfig) {
        return new Impl(
                EmbeddedKafka$.MODULE$.start(
                        new EmbeddedKafkaConfigImpl(
                                kafkaPort,
                                zooKeeperPort,
                                Maps.map(brokerConfig),
                                Maps.empty(),
                                Maps.empty())));
    }

    final class Impl implements EKafka {
        private static final Logger LOGGER = LoggerFactory.getLogger("ekafka");
        private final EmbeddedK ek;

        private Impl(EmbeddedK embeddedK) {
            ek = embeddedK;
        }

        @Override
        public String bootstrapServers() {
            return ek.broker().advertisedListeners().headOption()
                    .map(ep -> ep.host() + ":" + ep.port())
                    .get();
        }

        @Override
        public void stop() {
            LOGGER.debug("Stopping");
            ek.stop(true);
            LOGGER.debug("Stopped");
        }
    }
}
