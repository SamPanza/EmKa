package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import su.ptx.emka.junit.EkAdmin;
import su.ptx.emka.junit.EkBootstrapServers;
import su.ptx.emka.junit.EkConsumer;
import su.ptx.emka.junit.EkProducer;
import su.ptx.emka.junit.EmKaExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static su.ptx.emka.junit.EkConsumer.AutoOffsetReset.EARLIEST;

@ExtendWith(EmKaExtension.class)
class EmKaExtensionTests {
    @Test
    void withClients(@EkBootstrapServers String bServers,
                     @EkAdmin Admin admin,
                     @EkProducer Producer<String, String> producer,
                     @EkConsumer(autoOffsetReset = EARLIEST) Consumer<String, String> consumer) throws InterruptedException, ExecutionException {
        assertTrue(bServers.matches("localhost:\\d\\d\\d\\d+"));
        var topic = "Topeg";
        var numPartitions = 3;
        admin.createTopics(singleton(new NewTopic(topic, numPartitions, (short) 1))).all().get();
        for (var i = 0; i < numPartitions; ++i) {
            producer.send(new ProducerRecord<>(topic, i, Integer.toString(i), Integer.toString(i)));
        }
        Set<String> out = new HashSet<>();
        consumer.subscribe(singleton(topic));
        startNewThread(() -> {
            var consumed = 0;
            do {
                var consumerRecords = consumer.poll(ofSeconds(1));
                consumed += consumerRecords.count();
                consumerRecords.forEach(cr -> out.add(format("%d-%s-%s", cr.partition(), cr.key(), cr.value())));
            } while (consumed < numPartitions);
        }).join(10_000);
        assertEquals(Set.of("0-0-0", "1-1-1", "2-2-2"), out);
    }

    private static Thread startNewThread(Runnable task) {
        var t = new Thread(task);
        t.start();
        return t;
    }
}
