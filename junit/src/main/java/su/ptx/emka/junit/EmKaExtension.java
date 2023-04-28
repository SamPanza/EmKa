package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class EmKaExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    @Override
    public void beforeEach(ExtensionContext ctx) {
        System.err.println("beforeEach");
    }

    @Override
    public void afterEach(ExtensionContext ctx) {
        System.err.println("afterEach");
    }

    @Override
    public boolean supportsParameter(ParameterContext p, ExtensionContext e) {
        return p.isAnnotated(BootstrapServers.class);
    }

    @Override
    public String resolveParameter(ParameterContext p, ExtensionContext e) {
        return "ачо?";
    }
}
