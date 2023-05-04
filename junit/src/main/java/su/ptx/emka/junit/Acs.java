package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.util.ArrayDeque;
import java.util.Deque;

//TODO: tests
final class Acs implements CloseableResource {
    private final Deque<AutoCloseable> acs = new ArrayDeque<>();

    <T> T pass(T o) {
        if (o instanceof AutoCloseable ac) {
            acs.push(ac);
        }
        return o;
    }

    @Override
    public void close() {
        while (!acs.isEmpty()) {
            try {
                acs.pop().close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
