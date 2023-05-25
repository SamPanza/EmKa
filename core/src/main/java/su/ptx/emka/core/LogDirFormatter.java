package su.ptx.emka.core;

import static java.nio.file.Files.createTempDirectory;
import static java.util.Optional.empty;
import static org.apache.kafka.common.Uuid.randomUuid;
import static org.apache.kafka.metadata.bootstrap.BootstrapMetadata.fromVersion;
import static org.apache.kafka.server.common.MetadataVersion.latest;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import kafka.server.BrokerMetadataCheckpoint;
import kafka.server.MetaProperties;
import org.apache.kafka.metadata.bootstrap.BootstrapDirectory;

final class LogDirFormatter {
  static final String META_PROPS = "meta.properties";
  private final File dir;

  LogDirFormatter(File dir) {
    this.dir = dir;
  }

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
    if (!logDir.exists() && !logDir.mkdirs()) {
      throw new RuntimeException("Can't create directory " + logDir);
    }
    var metaProps = new File(logDir, META_PROPS);
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
