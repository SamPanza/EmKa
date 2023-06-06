package su.ptx.emka.clients.admin;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Uuid;

/**
 * TODO: javadoc.
 */
public final class CreatedTopic {
  private final CreateTopicsResult ctr;

  CreatedTopic(CreateTopicsResult ctr) {
    this.ctr = ctr;
  }

  /**
   * TODO: javadoc.
   */
  public String name() {
    var names = ctr.values().keySet();
    if (names.size() != 1) {
      throw new IllegalArgumentException(
          "Expected one topic in CTR but topics are %s".formatted(names));
    }
    return names.iterator().next();
  }

  public int numPartitions() {
    return sneakyGet(ctr.numPartitions(name()));
  }

  public int replicationFactor() {
    return sneakyGet(ctr.replicationFactor(name()));
  }

  public Uuid id() {
    return sneakyGet(ctr.topicId(name()));
  }

  private static <T> T sneakyGet(KafkaFuture<T> f) {
    try {
      return f.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted", e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
