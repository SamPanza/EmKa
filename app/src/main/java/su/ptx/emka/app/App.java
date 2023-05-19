package su.ptx.emka.app;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;

import java.io.File;
import org.eclipse.microprofile.config.ConfigProvider;
import su.ptx.emka.core.EmKaServer;

/**
 * EmKa standalone application.
 */
public class App {
  /**
   * main.
   *
   * @param args Not used
   */
  public static void main(String[] args) {
    var config = ConfigProvider.getConfig();
    int b = config.getOptionalValue("b", int.class).orElse(9092);
    int c = config.getOptionalValue("c", int.class).orElse(9093);
    //TODO: Empty string
    File d = config.getOptionalValue("d", File.class).orElse(new File("/tmp/emka"));
    out.format("%d %d %s\n", b, c, d);

    @SuppressWarnings("resource")
    var emKaServer = EmKaServer.create();
    emKaServer.run(b, c, d);
    out.println(emKaServer.bootstrapServers());

    getRuntime().addShutdownHook(new Thread(() -> {
      emKaServer.close();
      out.println("See ya!");
    }));
  }
}
