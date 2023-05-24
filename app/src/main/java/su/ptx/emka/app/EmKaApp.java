package su.ptx.emka.app;

import static java.lang.Runtime.getRuntime;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

import java.io.File;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.eclipse.microprofile.config.ConfigProvider;
import su.ptx.emka.core.EmKaServer;

/**
 * EmKa standalone application.
 */
public class EmKaApp implements Runnable, EmKaAppMBean {
  /**
   * main.
   */
  public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
    EmKaApp app;
    getPlatformMBeanServer().registerMBean(
      app = new EmKaApp(),
      new ObjectName(app.getClass().getPackageName(), "type", app.getClass().getSimpleName()));
    getRuntime().addShutdownHook(new Thread(app::shutdown));
    app.run();
    System.err.println("Running");
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
}
