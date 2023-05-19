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
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Optional;

import static java.nio.file.Files.createTempDirectory;

final class LogDir {
  private final File dir;

  LogDir(File dir) {
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
      //TODO: Compare nodeId with what's in logDir
      return logDir;
    }
    new BrokerMetadataCheckpoint(metaProps).write(
      new MetaProperties(Uuid.randomUuid().toString(), nodeId).toProperties());
    try {
      new BootstrapDirectory(logDir.toString(), Optional.empty()).writeBinaryFile(
        BootstrapMetadata.fromVersion(MetadataVersion.latest(), ""));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return logDir;
  }
}
