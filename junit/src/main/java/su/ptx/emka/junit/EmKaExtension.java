package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

public class EmKaExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final Namespace NS = Namespace.GLOBAL;
    private static final String K = "EmKaInstance";

    @Override
    public void beforeEach(ExtensionContext ec) throws Exception {
        //noinspection resource
        ec.getStore(NS).put(K, EmKa.create().start());
    }

    @Override
    public void afterEach(ExtensionContext ec) {
        ec.getStore(NS).get(K, EmKa.class).stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pc.isAnnotated(BootstrapServers.class);
    }

    @Override
    public String resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return ec.getStore(NS).get(K, EmKa.class).bootstrapServers();
    }
}
