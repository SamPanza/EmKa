package su.ptx.emka.app;

/**
 * TODO: javadoc.
 */
public interface EmKaAppMBean {
  boolean isReady();

  void shutdown();

  String getBootstrapServers();

  String getLogDir();
}
