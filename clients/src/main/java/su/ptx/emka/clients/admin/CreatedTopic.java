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
  private final String name;

  CreatedTopic(CreateTopicsResult createTopicsResult) {
    ctr = createTopicsResult;
    var names = ctr.values().keySet().iterator();
    name = names.next();
    assert !names.hasNext();
  }

  /**
   * TODO: javadoc.
   */
  public String name() {
    return name;
  }

  public int numPartitions() {
    return sneakyGet(ctr.numPartitions(name));
  }

  public int replicationFactor() {
    return sneakyGet(ctr.replicationFactor(name));
  }

  public Uuid id() {
    return sneakyGet(ctr.topicId(name));
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
