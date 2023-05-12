package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

final class ParameterResolverAdapter implements ParameterResolver {
    private final ParamRezolvr pr;

    ParameterResolverAdapter(ParamRezolvr pr) {
        this.pr = pr;
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.supports(ParamCtx.of(pc));
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.resolve(ParamCtx.of(pc), ExtCtx.of(ec));
    }
}
