package su.ptx.emka.junit.alien;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import su.ptx.emka.clients.admin.Kadm;
import su.ptx.emka.junit.EmKa;

@EmKa
@TestMethodOrder(OrderAnnotation.class)
class EmKaStopsAfterEachTestTests {
  @SuppressWarnings("unused")
  private Admin admin;

  private Kadm kadm;

  @BeforeEach
  void setUp() {
    kadm = new Kadm(admin);
  }

  @Test
  @Order(1)
  void createTopic() {
    kadm.createTopic("1");
  }

  @Test
  @Order(2)
  void createdTopicGone() {
    var e1 = assertThrows(RuntimeException.class, () -> kadm.topicInfo("1"));
    var e2 = e1.getCause();
    assertTrue(e2 instanceof ExecutionException);
    var e3 = e2.getCause();
    assertTrue(e3 instanceof UnknownTopicOrPartitionException);
    assertEquals("This server does not host this topic-partition.", e3.getMessage());
  }
}
