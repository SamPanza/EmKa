package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import su.ptx.emka.core.EmKa;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

interface ExtCtx {
    <T> T count(T o);

    String b_servers();

    static ExtCtx of(ExtensionContext ec) {
        final class Impl implements ExtCtx {
            private static final ExtensionContext.Namespace NS = create("su.ptx.emka");
            private final ExtensionContext ec;

            private Impl(ExtensionContext ec) {
                this.ec = ec;
            }

            @Override
            public <T> T count(T o) {
                return ofNullable(ec.getStore(NS).get("toClose", ToClose.class))
                        .orElseGet(() -> {
                            var toClose = new ToClose();
                            ec.getStore(NS).put("toClose", toClose);
                            return toClose;
                        })
                        .add(o);
            }

            @Override
            public String b_servers() {
                return ((EmKa) ec.getStore(NS).get("toClose", ToClose.class).head()).bootstrapServers();
            }

            private static final class ToClose implements ExtensionContext.Store.CloseableResource {
                private final Queue<AutoCloseable> acs = new LinkedBlockingQueue<>();

                private <T> T add(T o) {
                    if (o instanceof AutoCloseable ac) {
                        acs.add(ac);
                    }
                    return o;
                }

                Object head() {
                    return acs.element();
                }

                @Override
                public void close() {
                    while (!acs.isEmpty()) {
                        try {
                            acs.remove().close();
                        } catch (Exception e) {
                            //NB: Silence?
                        }
                    }
                }
            }
        }
        return new Impl(ec);
    }
}
