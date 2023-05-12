package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EmKaExtension;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static su.ptx.emka.junit.alien.U.startThreadAndWaitFor;

class ProducerAndConsumerAsParametersTest {
    @Test
    @ExtendWith(EmKaExtension.class)
    void test(Producer<Integer, Integer> p, Consumer<Integer, Integer> c) throws InterruptedException {
        assertNotNull(p);
        assertNotNull(c);
        var t = "1";
        var sent = IntStream.rangeClosed(1, 5).boxed().collect(toUnmodifiableSet());
        sent.stream()
                .map(v -> new ProducerRecord<Integer, Integer>(t, v))
                .forEach(p::send);
        Collection<ConsumerRecord<Integer, Integer>> received = new ArrayBlockingQueue<>(sent.size());
        startThreadAndWaitFor(() -> {
            c.subscribe(singleton(t));
            do {
                for (var cr : c.poll(Duration.ofSeconds(1))) {
                    received.add(cr);
                }
            } while (received.size() < sent.size());
        }, 10_000);
        assertEquals(
                sent,
                received.stream().map(ConsumerRecord::value).collect(toUnmodifiableSet()));
    }
}
