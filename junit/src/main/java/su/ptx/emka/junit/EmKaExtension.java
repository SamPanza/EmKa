package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.target.Target;

import static su.ptx.emka.junit.rezolvr.Rezolvrs.rezolvrs;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver {
    @Override
    public void beforeEach(ExtensionContext ec) {
        ExtCtx.of(ec).count(EmKa.create()).start();
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        return rezolvrs().anyMatch(r -> r.test(t));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        var t = Target.of(pc.getParameter());
        var c = ExtCtx.of(ec);
        return c.count(rezolvrs()
                .filter(r -> r.test(t)).map(r -> r.apply(t, c.b_servers()))
                .findFirst().orElseThrow());
    }
}
