package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.junit.rezolvr.AllRezolvrs;

import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;
import static su.ptx.emka.junit.ctx.Ctx.ofExtensionContext;
import static su.ptx.emka.junit.target.Target.ofField;
import static su.ptx.emka.junit.target.Target.ofParameter;

public final class EmKaExtension implements ParameterResolver, TestInstancePostProcessor {
    private final AllRezolvrs allRezolvrs = new AllRezolvrs();

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return allRezolvrs.test(ofParameter(pc.getParameter()));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return allRezolvrs.apply(ofParameter(pc.getParameter()), ofExtensionContext(ec)).orElseThrow();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext ec) {
        var c = ofExtensionContext(ec);
        for (var f : findFields(testInstance.getClass(), f -> true, BOTTOM_UP)) {
            allRezolvrs.apply(ofField(f), c).ifPresent(v -> {
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
