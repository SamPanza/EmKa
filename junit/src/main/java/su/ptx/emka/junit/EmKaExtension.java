package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.target.Target;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ModifierSupport.isNotFinal;
import static org.junit.platform.commons.support.ModifierSupport.isNotStatic;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;
import static su.ptx.emka.junit.rezolvr.Rezolvrs.rezolvrs;

public final class EmKaExtension implements ParameterResolver, TestInstancePostProcessor {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        return rezolvrs().anyMatch(r -> r.test(t));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        var c = Ec.of(ec);
        return rezolvrs()
                .filter(r -> r.test(t))
                .map(r -> r.apply(t, c.b_servers()))
                .map(c::count)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext ec) {
        var c = Ec.of(ec);
        c.count(EmKa.create()).start();
        Function<Field, ?> get = f -> {
            try {
                f.setAccessible(true);
                return f.get(testInstance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
        BiConsumer<Field, Object> set = (f, v) -> {
            try {
                f.set(testInstance, v);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
        for (var f : findFields(
                testInstance.getClass(),
                f -> isNotStatic(f) && isNotFinal(f) && get.apply(f) == null,
                BOTTOM_UP)) {
            var t = Target.of(f);
            rezolvrs()
                    .filter(r -> r.test(t))
                    .map(r -> r.apply(t, c.b_servers()))
                    .map(c::count)
                    .findFirst()
                    .ifPresent(v -> set.accept(f, v));
        }
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
