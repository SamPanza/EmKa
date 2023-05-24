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
    new EmKaApp().run();
  }

  private final EmKaServer server;

  private EmKaApp() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
    server = EmKaServer.create();
    getRuntime().addShutdownHook(new Thread(this::shutdown));
    var c = getClass();
    getPlatformMBeanServer().registerMBean(this, new ObjectName("%s:type=%s".formatted(c.getPackageName(), c.getSimpleName())));
  }

  @Override
  public void run() {
    var cfg = ConfigProvider.getConfig();
    server.run(
      cfg.getOptionalValue("emka.bro.port", int.class).orElse(0),
      cfg.getOptionalValue("emka.con.port", int.class).orElse(0),
      cfg.getOptionalValue("emka.log.dir", File.class).orElse(null));
  }

  @Override
  public void shutdown() {
    server.close();
  }
}
