package su.ptx.emka.junit.alien;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import su.ptx.emka.junit.EmKa;

@EmKa
@TestMethodOrder(OrderAnnotation.class)
class EmKaStopsAfterEachTestTests {
  @SuppressWarnings("unused")
  private Admin admin;

  @Test
  @Order(1)
  void createTopic() throws ExecutionException, InterruptedException {
    admin.createTopics(singleton(new NewTopic("1", 1, (short) 1))).all().get();
  }

  @Test
  @Order(2)
  void createdTopicGone() {
    var outerE = assertThrows(
        ExecutionException.class,
        () -> admin.describeTopics(singleton("1")).allTopicNames().get());
    var innerE = outerE.getCause();
    assertTrue(innerE instanceof UnknownTopicOrPartitionException);
  }
}
