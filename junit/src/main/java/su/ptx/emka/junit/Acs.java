package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.util.ArrayDeque;
import java.util.Deque;

final class Acs implements CloseableResource {
    private final Deque<AutoCloseable> acs = new ArrayDeque<>();

    <T> T pass(T o) {
        //TODO: Log what's passed
        if (o instanceof AutoCloseable ac) {
            //TODO: Log what's pushed
            acs.push(ac);
        }
        return o;
    }

    @Override
    public void close() throws Exception {
        while (!acs.isEmpty()) {
            //TODO: Log what's closed
            acs.pop().close();
        }
    }
}
