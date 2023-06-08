package su.ptx.emka.clients.admin;

import static java.util.Collections.singleton;
import static java.util.Optional.empty;
import static su.ptx.emka.clients.Kafut.sneakyGet;

import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.QuorumInfo;
import org.apache.kafka.clients.admin.TopicDescription;

/**
 * TODO: javadoc.
 */
public final class Kadm implements AutoCloseable {
  private final Admin adm;

  public Kadm(Admin admin) {
    adm = admin;
  }

  public Kadm(String b_servers) {
    this(Admin.create(Map.of("bootstrap.servers", b_servers)));
  }

  /**
   * TODO: javadoc.
   */
  public CreatedTopic createTopic(String name) {
    return new CreatedTopic(
        adm.createTopics(
            singleton(
                new NewTopic(
                    name,
                    empty(),
                    empty()))));
  }

  /**
   * TODO: javadoc.
   */
  public TopicDescription describeTopic(String name) {
    return sneakyGet(
        adm.describeTopics(
                singleton(name),
                new DescribeTopicsOptions().includeAuthorizedOperations(true))
            .topicNameValues()
            .get(name));
  }

  public QuorumInfo quorumInfo() {
    return sneakyGet(adm.describeMetadataQuorum().quorumInfo());
  }

  /**
   * TODO: javadoc.
   */
  public Map.Entry<String, LogDirDescription> logDir() {
    return sneakyGet(
        adm.describeLogDirs(singleton(1))
            .descriptions()
            .get(1))
        .entrySet()
        .iterator()
        .next();
  }

  @Override
  public void close() {
    adm.close();
  }
}
