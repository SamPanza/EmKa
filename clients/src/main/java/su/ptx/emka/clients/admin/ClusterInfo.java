package su.ptx.emka.clients.admin;

import static su.ptx.emka.clients.Kafut.sneakyGet;

import java.util.Collection;
import java.util.Set;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.acl.AclOperation;

/**
 * TODO: javadoc.
 */
public final class ClusterInfo {
  private final DescribeClusterResult dcr;

  ClusterInfo(DescribeClusterResult describeClusterResult) {
    dcr = describeClusterResult;
  }

  public Collection<Node> nodes() {
    return sneakyGet(dcr.nodes());
  }

  public Node controller() {
    return sneakyGet(dcr.controller());
  }

  public String id() {
    return sneakyGet(dcr.clusterId());
  }

  public Set<AclOperation> authorizedOps() {
    return sneakyGet(dcr.authorizedOperations());
  }
}
