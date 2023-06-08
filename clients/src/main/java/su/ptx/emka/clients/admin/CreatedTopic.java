package su.ptx.emka.clients.admin;

import static su.ptx.emka.clients.Kafut.sneakyGet;

import org.apache.kafka.clients.admin.CreateTopicsResult;
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
}
