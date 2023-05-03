package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.moar.DelegatingParameterResolver;

import static su.ptx.emka.junit.Ztore.Key.ACS;
import static su.ptx.emka.junit.Ztore.Key.B_SERVERS;
import static su.ptx.emka.junit.Ztore.NS;

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
        var acs = new Acs();
        ec.getStore(NS).put(ACS, acs);
        ec.getStore(NS).put(
                B_SERVERS,
                acs.pass(EmKa.create()).start().bootstrapServers());
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return ec.getStore(NS).get(ACS, Acs.class).pass(super.resolveParameter(pc, ec));
    }
}
