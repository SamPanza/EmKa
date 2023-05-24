package su.ptx.emka.app;

public interface EmKaAppMBean {
  default boolean isReady() {
    return true;
  }

  void shutdown();
}
