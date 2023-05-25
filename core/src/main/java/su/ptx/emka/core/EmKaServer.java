package su.ptx.emka.core;

import java.io.File;

/**
 * TODO: javadoc.
 */
public interface EmKaServer extends AutoCloseable {
  static EmKaServer create() {
    return new KRaftee();
  }

  String bootstrapServers();

  String logDirPath();

  default EmKaServer run() {
    return run(0, 0, null);
  }

  EmKaServer run(int port1, int port2, File dir);

  @Override
  void close();
}
