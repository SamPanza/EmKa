package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import su.ptx.emka.core.EmKa;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

interface ExtCtx {
    <T> T count(T o);

    String b_servers();

    static ExtCtx of(ExtensionContext ec) {
        final class KeptAutoCloseables implements Store.CloseableResource {
            private final Queue<AutoCloseable> kacs = new LinkedBlockingQueue<>();

            private <T> T keep(T o) {
                if (o instanceof AutoCloseable ac) {
                    kacs.add(ac);
                }
                return o;
            }

            Object head() {
                return kacs.element();
            }

            @Override
            public void close() {
                while (!kacs.isEmpty()) {
                    try {
                        kacs.remove().close();
                    } catch (Exception e) {
                        //TODO: log warn
                    }
                }
            }
        }
        var store = ec.getStore(GLOBAL);
        return new ExtCtx() {
            @Override
            public <T> T count(T o) {
                return kacs().keep(o);
            }

            @Override
            public String b_servers() {
                return ((EmKa) kacs().head()).bootstrapServers();
            }

            KeptAutoCloseables kacs() {
                return store.getOrComputeIfAbsent(KeptAutoCloseables.class);
            }
        };
    }
}
