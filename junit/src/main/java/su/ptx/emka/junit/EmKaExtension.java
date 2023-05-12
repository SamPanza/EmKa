package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.core.EmKa;

import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ModifierSupport.isNotFinal;
import static org.junit.platform.commons.support.ModifierSupport.isNotStatic;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver, TestInstancePostProcessor {
    private final ParameterResolver pr;
    private final FieldResolver[] frs;

    public EmKaExtension() {
        pr = new DelegatingParamRezolvr(
                new BootstrapServersParamRezolvr(),
                new AdminParamRezolvr(),
                new ProducerParamRezolvr(),
                new ConsumerParamRezolvr());
        frs = new FieldResolver[]{};
    }

    @Override
    public void beforeEach(ExtensionContext ec) {
        var acs = V.acs.put(ec, new Acs());
        var emKa = EmKa.create();
        acs.pass(emKa);
        emKa.start();
        V.b_servers.put(ec, emKa.bootstrapServers());
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pr.supportsParameter(pc, ec);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return V.acs.get(ec).pass(pr.resolveParameter(pc, ec));
    }

    @Override
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
    }
}
