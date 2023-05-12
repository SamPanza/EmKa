package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver/*, TestInstancePostProcessor*/ {
    private final ParameterResolver pr;
    //private final FieldResolver[] frs;

    public EmKaExtension() {
        pr = new DelegatingParamRezolvr(
                new BootstrapServersParamRezolvr(),
                new AdminParamRezolvr(),
                new ProducerParamRezolvr(),
                new ConsumerParamRezolvr());
        //frs = new FieldResolver[]{};
    }

    @Override
    public void beforeEach(ExtensionContext ec) {
        ExtCtx.of(ec).beforeEach();
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.supportsParameter(pc, ec);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.resolveParameter(pc, ec);
    }

    /*@Override
    public void postProcessTestInstance(Object instance, ExtensionContext ec) {
        var fcs = findFields(instance.getClass(), f -> isNotStatic(f) && isNotFinal(f), BOTTOM_UP).stream()
                .map(f -> new FieldContext(f, instance))
                .filter(fc -> fc.get() == null)
                .toArray(FieldContext[]::new);
        for (var fr : frs) {
            for (var fc : fcs) {
                if (fr.supports(fc, ec)) {
                    fc.set(fr.resolve(fc, ec));
                }
            }
        }
    }*/
}
