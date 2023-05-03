package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import su.ptx.emka.core.EmKa;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

final class U {
    private static final Namespace NS = create("su.ptx.emka");

    private enum K {
        b_servers,
        acs
    }

    private U() {
    }

    static String b_servers(ExtensionContext ec) {
        return ec.getStore(NS).get(K.b_servers, String.class);
    }

    static Object pass(ExtensionContext ec, Object o) {
        //TODO: Log what's passed
        if (o instanceof EmKa emKa) {
            ec.getStore(NS).put(K.b_servers, emKa.bootstrapServers());
            new Acs(ec);
        }
        return acs(ec).pass(o);
    }

    private static Acs acs(ExtensionContext ec) {
        return ec.getStore(NS).get(K.acs, Acs.class);
    }

    private static final class Acs implements CloseableResource {
        private final Deque<AutoCloseable> acs = new ArrayDeque<>();

        private Acs(ExtensionContext ec) {
            if (acs(ec) != null) {
                throw new IllegalStateException("Store already has value with key " + K.acs);
            }
            ec.getStore(NS).put(K.acs, this);
        }

        private Object pass(Object o) {
            if (o instanceof AutoCloseable ac) {
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
}
