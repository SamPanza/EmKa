package su.ptx.emka.junit.alien;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import su.ptx.emka.junit.EmKa;

@EmKa
class AllClientsAsParamsTests {
  @Test
  void create_produce_consume(Admin admin, Producer<Integer, Integer> producer, Consumer<Integer, Integer> consumer) throws ExecutionException, InterruptedException {
    //[The French definite articles](https://www.thinkinfrench.com/grammar-lesson/french-definite-articles/) are
    //  Le (masculine singular)
    //  La (feminine singular)
    //  L’ (followed by a vowel)
    //  Les (plural)
    var topic = "Le.Topique";

    var createTopicsResult = admin.createTopics(singleton(new NewTopic(topic, empty(), empty())));
    assertEquals(1, createTopicsResult.numPartitions(topic).get());
    assertEquals(1, createTopicsResult.replicationFactor(topic).get());

    var sent = new Random().ints().limit(100).boxed().collect(toUnmodifiableSet());
    Set<Integer> polled = new HashSet<>();
    var poller = new Thread(() -> {
      consumer.subscribe(singleton(topic));
      do {
        for (var consumerRecord : consumer.poll(ofSeconds(1))) {
          polled.add(consumerRecord.value());
        }
      } while (polled.size() < sent.size());
    });
    poller.start();

    sent.stream()
      .map(v -> new ProducerRecord<Integer, Integer>(topic, v))
      .forEach(producer::send);

    poller.join(10_000);
    assertEquals(sent, polled);
  }
}
