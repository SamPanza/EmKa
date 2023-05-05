package su.ptx.emka.app;

import org.eclipse.microprofile.config.ConfigProvider;
import su.ptx.emka.core.KRaftee;

import java.io.File;

import static java.lang.System.out;

public class App {
    public static void main(String[] args) {
        var config = ConfigProvider.getConfig();
        int brop = config.getOptionalValue("brop", int.class).orElse(0);
        int conp = config.getOptionalValue("conp", int.class).orElse(0);
        File logd = config.getOptionalValue("logd", File.class).orElse(null);
        out.format("brop=%d, conp=%d, logd=%s\n", brop, conp, logd);

        @SuppressWarnings("resource")
        var k = new KRaftee();
        k.start(brop, conp, logd);
        out.println(k.bootstrapServers());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            k.stop();
            out.println("See ya!");
        }));
    }
}
