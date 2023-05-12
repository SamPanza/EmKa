package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import su.ptx.emka.core.EmKa;

public final class EmKaExtension extends DelegatingParameterResolver implements BeforeEachCallback {
    public EmKaExtension() {
        super(
                new BootstrapServersParameterResolver(),
                new AdminParameterResolver(),
                new ProducerParameterResolver(),
                new ConsumerParameterResolver());
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
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return V.acs.get(ec).pass(super.resolveParameter(pc, ec));
    }
}
