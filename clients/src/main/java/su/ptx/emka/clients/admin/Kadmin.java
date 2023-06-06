package su.ptx.emka.clients.admin;

import static java.util.Collections.singleton;
import static java.util.Optional.empty;

import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;

/**
 * TODO: javadoc.
 */
public final class Kadmin {
  private final Admin admin;

  public Kadmin(Admin admin) {
    this.admin = admin;
  }

  public Kadmin(String b_servers) {
    this(Admin.create(Map.of("bootstrap.servers", b_servers)));
  }

  /**
   * TODO: javadoc.
   */
  public CreatedTopic createTopic(String name) {
    return new CreatedTopic(
        admin.createTopics(
            singleton(
                new NewTopic(
                    name,
                    empty(),
                    empty()))));
  }
}
