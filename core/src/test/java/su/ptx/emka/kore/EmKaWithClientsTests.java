package su.ptx.emka.kore;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKa;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmKaWithClientsTests {
    @Test
    void create_produce_consume() throws Exception {
        try (var emKa = EmKa.create().start()) {
            var bServers = emKa.bootstrapServers();
            var topic = "Topeg";
            var numPartitions = 3;

            try (var admin = Admin.create(Map.of("bootstrap.servers", bServers))) {
                admin.createTopics(singleton(new NewTopic(topic, numPartitions, (short) 1))).all().get();
            }

            try (Producer<String, String> producer = new KafkaProducer<>(Map.of(
                    "key.serializer", StringSerializer.class,
                    "value.serializer", StringSerializer.class,
                    "bootstrap.servers", bServers))) {
                for (var i = 0; i < numPartitions; ++i) {
                    producer.send(new ProducerRecord<>(topic, i, Integer.toString(i), Integer.toString(i)));
                }
            }

            try (Consumer<String, String> consumer = new KafkaConsumer<>(Map.of(
                    "key.deserializer", StringDeserializer.class,
                    "value.deserializer", StringDeserializer.class,
                    "bootstrap.servers", bServers,
                    "group.id", "G1",
                    "auto.offset.reset", "earliest"))) {
                consumer.subscribe(singleton(topic));
                Set<String> out = new HashSet<>();
                startNewThread(() -> {
                    var consumed = 0;
                    do {
                        var consumerRecords = consumer.poll(ofSeconds(1));
                        consumed += consumerRecords.count();
                        consumerRecords.forEach(cr -> out.add(format("%d-%s-%s", cr.partition(), cr.key(), cr.value())));
                    } while (consumed < numPartitions);
                }).join(10_000);
                assertEquals(
                        Set.of("0-0-0", "1-1-1", "2-2-2"),
                        out);
            }
        }
    }

    private static Thread startNewThread(Runnable task) {
        var t = new Thread(task);
        t.start();
        return t;
    }
}
