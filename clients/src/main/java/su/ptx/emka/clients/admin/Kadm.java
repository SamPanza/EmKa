package su.ptx.emka.clients.admin;

import static java.util.Collections.singleton;
import static java.util.Optional.empty;
import static su.ptx.emka.aux.Pair.pair;
import static su.ptx.emka.clients.Kafut.sneakyGet;

import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.QuorumInfo;
import org.apache.kafka.clients.admin.TopicDescription;
import su.ptx.emka.aux.Node;
import su.ptx.emka.aux.Pair;

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
  public TopicDescription topicInfo(String name) {
    return sneakyGet(
        adm.describeTopics(
                singleton(name),
                new DescribeTopicsOptions().includeAuthorizedOperations(true))
            .topicNameValues()
            .get(name));
  }

  /**
   * TODO: javadoc.
   */
  public Pair<String, LogDirDescription> logDir() {
    return pair(
        sneakyGet(
            adm.describeLogDirs(singleton(Node.id))
                .descriptions()
                .get(Node.id))
            .entrySet()
            .iterator()
            .next());
  }

  /**
   * TODO: javadoc.
   */
  public ClusterInfo clusterInfo() {
    return new ClusterInfo(
        adm.describeCluster(
            new DescribeClusterOptions().includeAuthorizedOperations(true)));
  }

  public QuorumInfo quorumInfo() {
    return sneakyGet(adm.describeMetadataQuorum().quorumInfo());
  }

  @Override
  public void close() {
    adm.close();
  }
}
