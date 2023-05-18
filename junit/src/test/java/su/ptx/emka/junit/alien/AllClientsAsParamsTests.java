package su.ptx.emka.junit.alien;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import su.ptx.emka.junit.EmKa;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EmKa
class AllClientsAsParamsTests {
    @Test
    void create_produce_consume(Admin admin, Producer<Integer, Integer> producer, Consumer<Integer, Integer> consumer) throws InterruptedException, ExecutionException {
        //[The French definite articles](https://www.thinkinfrench.com/grammar-lesson/french-definite-articles/) are
        //  Le (masculine singular)
        //  La (feminine singular)
        //  L’ (followed by a vowel)
        //  Les (plural)
        var topic = "Le.Topique";

        var createTopicsResult = admin.createTopics(singleton(new NewTopic(topic, empty(), empty())));
        assertEquals(1, createTopicsResult.numPartitions(topic).get());
        assertEquals(1, createTopicsResult.replicationFactor(topic).get());

        var values = Set.of(0);
        for (var value : values) {
            var record = new ProducerRecord<Integer, Integer>(topic, value);
            producer.send(record).get();
        }

        Set<Integer> consumedValues = new HashSet<>();
        var poller = new Thread(() -> {
            consumer.subscribe(singleton(topic));
            do {
                for (var consumerRecord : consumer.poll(ofSeconds(5))) {
                    consumedValues.add(consumerRecord.value());
                }
            } while (consumedValues.size() < values.size());
        });
        poller.start();
        poller.join(10_000);

        assertEquals(values, consumedValues);
    }
}
