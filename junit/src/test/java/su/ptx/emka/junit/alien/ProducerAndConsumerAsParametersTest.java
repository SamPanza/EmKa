package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;
import su.ptx.emka.junit.Konsumer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static su.ptx.emka.junit.alien.U.startThreadAndWaitFor;

class ProducerAndConsumerAsParametersTest {
    private static final String TOPIC = "1";

    @Test
    @ExtendWith(EmKaExtension.class)
    @DisplayName("produce & consume")
    void test(Producer<Integer, Integer> p,
              @Konsumer(subsribeTo = TOPIC) Consumer<Integer, Integer> c) throws InterruptedException {
        assertNotNull(p);
        assertNotNull(c);
        var sent = Set.of(1, 2, 3, 4, 5);
        sent.stream()
                .map(v -> new ProducerRecord<Integer, Integer>(TOPIC, v))
                .forEach(p::send);
        Set<Integer> received = new HashSet<>();
        startThreadAndWaitFor(() -> {
            do {
                for (var cr : c.poll(Duration.ofSeconds(1))) {
                    received.add(cr.value());
                }
            } while (received.size() < sent.size());
        }, 10_000);
        assertEquals(sent, received);
    }
}
