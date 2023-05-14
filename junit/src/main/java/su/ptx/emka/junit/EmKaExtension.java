package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver {
    private final ParameterResolver pr;

    public EmKaExtension() {
        pr = new DelegatingParamRezolvr(
                new BootstrapServersParamRezolvr(),
                new AdminParamRezolvr(),
                new ProducerParamRezolvr(),
                new ConsumerParamRezolvr());
    }

    @Override
    public void beforeEach(ExtensionContext ec) {
        ExtCtx.of(ec).count(EmKa.create()).start();
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.supportsParameter(pc, ec);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return ExtCtx.of(ec).count(pr.resolveParameter(pc, ec));
    }
}
