package su.ptx.emka.app;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import su.ptx.emka.core.EmKaServer;

import java.io.File;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;

/**
 * EmKa standalone application.
 */
public class App {
  private static final Config cfg = ConfigProvider.getConfig();
  private static final int port1 = cfg.getOptionalValue("emka.bro.port", int.class).orElse(0);
  private static final int port2 = cfg.getOptionalValue("emka.con.port", int.class).orElse(0);
  private static final File logDir = cfg.getOptionalValue("emka.log.dir", File.class).orElse(null);

  /**
   * main.
   *
   * @param args Not used
   */
  public static void main(String[] args) {
    var emKaServer = EmKaServer.create();
    emKaServer.run(port1, port2, logDir);
    out.println(emKaServer);

    getRuntime().addShutdownHook(new Thread(() -> {
      emKaServer.close();
      out.println("See ya!");
    }));
  }
}
