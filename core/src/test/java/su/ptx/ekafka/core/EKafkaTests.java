package su.ptx.ekafka.core;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EKafkaTests {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger("ekafka.tests");
    private EKafka eKafka;

    @BeforeEach
    void setUp() {
        eKafka = EKafka.start(0, 0, Map.of("auto.create.topics.enable", "false"));
    }

    @AfterEach
    void tearDown() {
        eKafka.close();
    }

    @Test
    void bootstrapServers_looks_OK() {
        assertTrue(
                Pattern.compile("localhost:\\d\\d\\d\\d+")
                        .matcher(eKafka.bootstrapServers())
                        .matches());
    }

    @Test
    void create_produce_consume() throws InterruptedException, ExecutionException {
        final var TOPIC = "t-1";
        final var N = 3;

        try (var admin = Admin.create(Map.of(
                "bootstrap.servers", eKafka.bootstrapServers()))) {
            admin.createTopics(singleton(new NewTopic(TOPIC, N, (short) 1))).all().get();
        }

        try (Producer<String, String> producer = new KafkaProducer<>(Map.of(
                "key.serializer", StringSerializer.class,
                "value.serializer", StringSerializer.class,
                "bootstrap.servers", eKafka.bootstrapServers()))) {
            for (var i = 0; i < N; ++i) {
                producer.send(new ProducerRecord<>(TOPIC, i, Integer.toString(i), Integer.toString(i)));
            }
        }

        @SuppressWarnings("resource")
        Consumer<String, String> consumer = new KafkaConsumer<>(Map.of(
                "key.deserializer", StringDeserializer.class,
                "value.deserializer", StringDeserializer.class,
                "bootstrap.servers", eKafka.bootstrapServers(),
                "group.id", "g-1",
                "auto.offset.reset", "earliest"));
        consumer.subscribe(singleton(TOPIC));
        Set<String> log = new HashSet<>();
        startNewThread(() -> {
            var polled = 0;
            do {
                var consumerRecords = consumer.poll(ofSeconds(1));
                polled += consumerRecords.count();
                consumerRecords.forEach(cr -> log.add(format("%d-%s-%s", cr.partition(), cr.key(), cr.value())));
            } while (polled < N);
            consumer.close();
        }).join(5000);
        assertEquals(Set.of("0-0-0", "1-1-1", "2-2-2"), log);
    }

    private static Thread startNewThread(Runnable task) {
        var t = new Thread(task);
        t.start();
        return t;
    }
}
