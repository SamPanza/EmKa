package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.target.Target;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
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
                .map(c::pass)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext ec) {
        var c = Ec.of(ec);
        for (var f : findFields(
                testInstance.getClass(),
                f -> isNotStatic(f) && isNotFinal(f) && new InstanceField(testInstance, f).get() == null,
                BOTTOM_UP)) {
            var t = Target.of(f);
            rezolvrs()
                    .filter(r -> r.test(t))
                    .map(r -> r.apply(t, c.b_servers()))
                    .map(c::pass)
                    .findFirst()
                    .ifPresent(new InstanceField(testInstance, f));
        }
    }

    private interface Ec {
        <T> T pass(T o);

        String b_servers();

        static Ec of(ExtensionContext ec) {
            final class Acs implements CloseableResource {
                private final Queue<AutoCloseable> acs = new LinkedBlockingQueue<>();

                private <T> T pass(T o) {
                    if (o instanceof EmKa) {
                        System.err.println("EmKa passed");
                    }
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
                            //TODO: log warn
                        }
                    }
                    System.err.println("Acs closed");
                }
            }
            return new Ec() {
                private static final Namespace NS = create("su.ptx.emka");

                @Override
                public <T> T pass(T o) {
                    return ec.getStore(NS).getOrComputeIfAbsent(Acs.class).pass(o);
                }

                @Override
                public synchronized String b_servers() {
                    return ec.getStore(NS).getOrComputeIfAbsent(
                            "b_servers",
                            k -> pass(EmKa.create()).start().bootstrapServers(),
                            String.class);

                }
            };
        }
    }
}
