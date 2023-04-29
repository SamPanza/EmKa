package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

public class EmKaExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final Namespace NS = Namespace.create("emka");

    @Override
    public void beforeEach(ExtensionContext e) throws Exception {
        @SuppressWarnings("resource")//see afterEach
        var emKa = EmKa.create();
        e.getStore(NS).put("emka", emKa.start());
    }

    @Override
    public void afterEach(ExtensionContext e) {
        e.getStore(NS).get("emka", EmKa.class).close();
    }

    @Override
    public boolean supportsParameter(ParameterContext p, ExtensionContext e) {
        return p.isAnnotated(BootstrapServers.class);
    }

    @Override
    public String resolveParameter(ParameterContext p, ExtensionContext e) {
        return e.getStore(NS).get("emka", EmKa.class).bootstrapServers();
    }
}
