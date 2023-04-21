package su.ptx.ekafka.core;

import kafka.zookeeper.ZooKeeperClientTimeoutException;
import org.junit.jupiter.api.Test;

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
        withBusyPort(port -> {
            //noinspection resource
            assertThrows(
                    ZooKeeperClientTimeoutException.class,
                    () -> EKafka.start(0, port, emptyMap()));
        });
    }

    //TODO: fails_to_start_on_busy_Kafka_port

    private static void withBusyPort(IntConsumer body) throws InterruptedException {
        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(null);
            var latch = new CountDownLatch(1);
            var acceptingThread = new Thread(() -> {
                try {
                    latch.countDown();
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
            try {
                body.accept(ssc.socket().getLocalPort());
            } finally {
                acceptingThread.interrupt();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
