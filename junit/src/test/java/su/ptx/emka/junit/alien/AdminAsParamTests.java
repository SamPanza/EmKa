package su.ptx.emka.junit.alien;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Test;
import su.ptx.emka.junit.EmKa;

@EmKa
class AdminAsParamTests {
  @Test
  void resolvedAndOperates(Admin admin) throws ExecutionException, InterruptedException {
    assertNotNull(admin);
    var cluster = admin.describeCluster();

    var id = cluster.clusterId().get();
    assertDoesNotThrow(() -> Uuid.fromString(id));

    var nodes = cluster.nodes().get();
    assertEquals(1, nodes.size());

    var node = nodes.iterator().next();
    assertEquals(1, node.id());
    assertEquals("localhost", node.host());
    assertFalse(node.hasRack()); //NB: What is rack?
    assertEquals(node, cluster.controller().get());

    assertNull(cluster.authorizedOperations().get());
  }
}
