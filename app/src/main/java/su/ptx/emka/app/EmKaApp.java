package su.ptx.emka.app;

import static java.lang.Runtime.getRuntime;

import java.io.File;
import org.eclipse.microprofile.config.ConfigProvider;
import su.ptx.emka.server.EmKaServer;

/**
 * TODO: javadoc.
 */
public class EmKaApp implements Runnable, EmKaAppMBean {
  /**
   * TODO: javadoc.
   */
  public static void main(String[] args) {
    EmKaApp app = new EmKaApp();
    Jmx.register(app);
    getRuntime().addShutdownHook(new Thread(app::shutdown));
    app.run();
  }

  private final EmKaServer server;
  private boolean ready;

  private EmKaApp() {
    server = EmKaServer.create();
  }

  @Override
  public void run() {
    var cfg = ConfigProvider.getConfig();
    server.run(
        cfg.getOptionalValue("emka.bro.port", int.class).orElse(0),
        cfg.getOptionalValue("emka.con.port", int.class).orElse(0),
        cfg.getOptionalValue("emka.log.dir", File.class).orElse(null));
    ready = true;
  }

  @Override
  public boolean isReady() {
    return ready;
  }

  @Override
  public void shutdown() {
    ready = false;
    server.close();
  }

  @Override
  public String getBootstrapServers() {
    return server.bootstrapServers();
  }

  @Override
  public String getLogDir() {
    return server.logDir();
  }
}
