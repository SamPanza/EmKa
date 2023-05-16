package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.rezolvr.Rezolvrs;
import su.ptx.emka.junit.target.Target;

import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;

public final class EmKaExtension implements ParameterResolver, TestInstancePostProcessor {
    private final Rezolvrs r = new Rezolvrs();

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return r.test(Target.of(pc.getParameter()));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return r.apply(Target.of(pc.getParameter()), Ctx.of(ec)).orElseThrow();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext ec) {
        var c = Ctx.of(ec);
        for (var f : findFields(testInstance.getClass(), f -> true, BOTTOM_UP)) {
            r.apply(Target.of(f), c).ifPresent(v -> {
                f.setAccessible(true);
                try {
                    f.set(testInstance, v);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
