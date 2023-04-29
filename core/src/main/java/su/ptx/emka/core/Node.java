package su.ptx.emka.core;

import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.MetaProperties;
import kafka.tools.StorageTool;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;
import org.apache.kafka.metadata.bootstrap.BootstrapMetadata;
import org.apache.kafka.server.common.MetadataVersion;
import scala.collection.immutable.Seq;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Optional;

final class Node {
    static final Node _1 = new Node(1);
    final int id;

    private Node(int id) {
        this.id = id;
    }

    /**
     * See {@link StorageTool#formatCommand(PrintStream, Seq, MetaProperties, MetadataVersion, boolean)}
     */
    File formatTmpLogDir() throws Exception {
        var dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        new BrokerMetadataCheckpoint(new File(dir, "meta.properties")).write(
                new MetaProperties(Uuid.randomUuid().toString(), id).toProperties());
        new BootstrapDirectory(
                dir.toString(),
                Optional.empty())
                .writeBinaryFile(BootstrapMetadata.fromVersion(MetadataVersion.latest(), ""));
        return dir;
    }
}
