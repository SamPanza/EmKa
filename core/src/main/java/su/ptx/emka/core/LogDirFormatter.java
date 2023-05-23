package su.ptx.emka.core;

import static java.nio.file.Files.createTempDirectory;
import static java.util.Optional.empty;
import static org.apache.kafka.common.Uuid.randomUuid;
import static org.apache.kafka.metadata.bootstrap.BootstrapMetadata.fromVersion;
import static org.apache.kafka.server.common.MetadataVersion.latest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.MetaProperties;
import kafka.tools.StorageTool;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;
import org.apache.kafka.server.common.MetadataVersion;
import scala.collection.immutable.Seq;

final class LogDirFormatter {
  private final File dir;

  LogDirFormatter(File dir) {
    this.dir = dir;
  }

  /**
   * See {@link StorageTool#formatCommand(PrintStream, Seq, MetaProperties, MetadataVersion, boolean)}
   */
  File format(int nodeId) {
    final File logDir;
    if (dir == null) {
      try {
        logDir = createTempDirectory(null).toFile();
        logDir.deleteOnExit();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      logDir = dir;
    }
    //TODO: Don't ignore result
    //noinspection ResultOfMethodCallIgnored
    logDir.mkdirs();
    var metaProps = new File(logDir, "meta.properties");
    if (metaProps.exists()) {
      return logDir;
    }
    new BrokerMetadataCheckpoint(metaProps).write(
      new MetaProperties(randomUuid().toString(), nodeId).toProperties());
    try {
      new BootstrapDirectory(logDir.toString(), empty()).writeBinaryFile(fromVersion(latest(), ""));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return logDir;
  }
}
