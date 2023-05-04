package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import su.ptx.emka.core.EmKa;

import static su.ptx.emka.junit.StoredValue.ACS;
import static su.ptx.emka.junit.StoredValue.B_SERVERS;

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
        var acs = ACS.put(ec, new Acs());
        var emKa = EmKa.create();
        acs.pass(emKa);
        emKa.start();
        B_SERVERS.put(ec, emKa.bootstrapServers());
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return ACS.get(ec).pass(super.resolveParameter(pc, ec));
    }
}
