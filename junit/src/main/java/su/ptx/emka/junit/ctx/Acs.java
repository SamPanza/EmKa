package su.ptx.emka.junit.ctx;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

final class Acs implements CloseableResource {
    private final Queue<AutoCloseable> acs = new LinkedBlockingQueue<>();

    <T> T pass(T o) {
        if (o instanceof AutoCloseable ac) {
            acs.add(ac);
        }
        return o;
    }

    @Override
    public void close() {
        while (!acs.isEmpty()) {
            try {
                acs.remove().close();
            } catch (Exception e) {
                //
            }
        }
    }
}
