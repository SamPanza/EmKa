package su.ptx.emka.app;

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
   */
  public static void main(String[] args) {
    var config = ConfigProvider.getConfig();
    int brop = config.getOptionalValue("brop", int.class).orElse(9092);
    int conp = config.getOptionalValue("conp", int.class).orElse(9093);
    File logd = config.getOptionalValue("logd", File.class).orElse(new File("/tmp/emka"));
    out.format("brop=%d, conp=%d, logd=%s\n", brop, conp, logd);

    @SuppressWarnings("resource")
    var emKaServer = EmKaServer.create();
    emKaServer.run(brop, conp, logd);
    out.println(emKaServer.bootstrapServers());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      emKaServer.close();
      out.println("See ya!");
    }));
  }
}
