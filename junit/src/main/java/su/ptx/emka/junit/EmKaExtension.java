package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import su.ptx.emka.core.EmKa;
import su.ptx.emka.junit.moar.DelegatingParameterResolver;

//TODO: Resolved parameters aren't passed so unclosed
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
        //noinspection resource
        U.pass(ec, EmKa.create().start());
    }
}
