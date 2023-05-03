package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.moar.DelegatingParameterResolver;

import static su.ptx.emka.junit.Ztore.acs;
import static su.ptx.emka.junit.Ztore.b_servers;

public final class EmKaExtension extends DelegatingParameterResolver implements BeforeEachCallback {
    public EmKaExtension() {
        super(
                new BootstrapServersParameterResolver(),
                new AdminParameterResolver(),
                new ProducerParameterResolver(),
                new ConsumerParameterResolver());
    }

    @Override
    public void beforeEach(ExtensionContext ec) throws Exception {
        var acs = acs(ec, new Acs());
        var emKa = EmKa.create();
        acs.pass(emKa);
        emKa.start();
        b_servers(ec, emKa.bootstrapServers());
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return acs(ec).pass(super.resolveParameter(pc, ec));
    }
}
