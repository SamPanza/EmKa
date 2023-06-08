package su.ptx.emka.junit.alien;

import static org.apache.kafka.common.acl.AclOperation.ALTER;
import static org.apache.kafka.common.acl.AclOperation.ALTER_CONFIGS;
import static org.apache.kafka.common.acl.AclOperation.CLUSTER_ACTION;
import static org.apache.kafka.common.acl.AclOperation.CREATE;
import static org.apache.kafka.common.acl.AclOperation.DESCRIBE;
import static org.apache.kafka.common.acl.AclOperation.DESCRIBE_CONFIGS;
import static org.apache.kafka.common.acl.AclOperation.IDEMPOTENT_WRITE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Test;
import su.ptx.emka.aux.Node;
import su.ptx.emka.clients.admin.Kadm;
import su.ptx.emka.junit.EmKa;

@EmKa
class AdminAsParamTests {
  @Test
  void resolvedAndOperates(Admin admin) {
    var ci = new Kadm(admin).clusterInfo();
    assertDoesNotThrow(() -> Uuid.fromString(ci.id()));
    var nodes = ci.nodes();
    assertEquals(1, nodes.size());
    var node = nodes.iterator().next();
    assertEquals(Node.id, node.id());
    assertEquals(Node.host, node.host());
    assertFalse(node.hasRack()); //NB: What is rack?
    assertEquals(node, ci.controller());
    assertEquals(
        Set.of(
            ALTER, ALTER_CONFIGS, DESCRIBE, DESCRIBE_CONFIGS,
            CREATE, IDEMPOTENT_WRITE, CLUSTER_ACTION),
        ci.authorizedOps());
  }
}
