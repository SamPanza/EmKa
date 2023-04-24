package su.ptx.ekafka.core;

import kafka.zookeeper.ZooKeeperClientTimeoutException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EKafkaTests {
    private static final Logger LOGGER = LoggerFactory.getLogger("ekafka.tests");

    @Test
    void start_and_stop() {
        try (var ek = EKafka.start(0, 0, emptyMap())) {
            assertTrue(
                    Pattern.compile("localhost:\\d\\d\\d\\d+")
                            .matcher(ek.bootstrapServers())
                            .matches());
        }
    }

    @Test
    void fails_to_start_on_busy_ZK_port() throws InterruptedException {
        //noinspection resource
        withBusyPort(port ->
                assertThrows(
                        ZooKeeperClientTimeoutException.class,
                        () -> EKafka.start(0, port, emptyMap())));
    }

    //TODO: fails_to_start_on_busy_Kafka_port

    private static void withBusyPort(IntConsumer body) throws InterruptedException {
        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(null);
            LOGGER.debug("Bound to {}", ssc.getLocalAddress());
            var latch = new CountDownLatch(1);
            var acceptingThread = new Thread(() -> {
                try {
                    latch.countDown();
                    LOGGER.debug("Waiting threads released");
                    while (true) {
                        ssc.accept();
                    }
                } catch (ClosedByInterruptException e) {
                    //it's ok
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            acceptingThread.start();
            latch.await();
            var port = ssc.socket().getLocalPort();
            LOGGER.debug("Calling body.accept({})", port);
            try {
                body.accept(port);
            } finally {
                acceptingThread.interrupt();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
