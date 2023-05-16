package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;
import su.ptx.emka.junit.Konsumer;

import java.util.HashSet;
import java.util.Set;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientsAsParamsTest {
    private static final String topic = "1";

    @Test
    @ExtendWith(EmKaExtension.class)
    @DisplayName("consume & produce")
    void test(Producer<Integer, Integer> p,
              @Konsumer(subsribeTo = topic) Consumer<Integer, Integer> c) throws InterruptedException {
        var sent = Set.of(1, 2, 3, 4, 5);
        Set<Integer> received = new HashSet<>();
        var poll = new Thread(() -> {
            do {
                for (var cr : c.poll(ofSeconds(5))) {
                    received.add(cr.value());
                }
            } while (received.size() < sent.size());
        });
        poll.start();
        sent.stream()
                .map(v -> new ProducerRecord<Integer, Integer>(topic, v))
                .forEach(p::send);
        poll.join(10_000);
        assertEquals(sent, received);
    }
}
