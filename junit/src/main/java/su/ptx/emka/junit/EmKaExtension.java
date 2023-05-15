package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.target.Target;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static su.ptx.emka.junit.rezolvr.Rezolvrs.rezolvrs;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver {
    @Override
    public void beforeEach(ExtensionContext ec) {
        Ec.of(ec).count(EmKa.create()).start();
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        return rezolvrs().anyMatch(r -> r.test(t));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        var c = Ec.of(ec);
        return c.count(rezolvrs()
                .filter(r -> r.test(t)).map(r -> r.apply(t, c.b_servers()))
                .findFirst().orElseThrow());
    }

    private interface Ec {
        <T> T count(T o);

        String b_servers();

        static Ec of(ExtensionContext ec) {
            final class Kacs implements CloseableResource {
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
            return new Ec() {
                @Override
                public <T> T count(T o) {
                    return kacs().keep(o);
                }

                @Override
                public String b_servers() {
                    return ((EmKa) kacs().head()).bootstrapServers();
                }

                Kacs kacs() {
                    return store.getOrComputeIfAbsent(Kacs.class);
                }
            };
        }
    }
}
