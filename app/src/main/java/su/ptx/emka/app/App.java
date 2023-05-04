package su.ptx.emka.app;

import su.ptx.emka.core.KRaftee;

import static java.lang.System.out;

public class App {
    public static void main(String[] args) {
        @SuppressWarnings("resource")
        var k = new KRaftee();
        k.start(0, 0, null);
        out.println(k.bootstrapServers());
    }
}
