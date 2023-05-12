package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

final class BootstrapServersParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pc.isAnnotated(EkBootstrapServers.class);
    }

    @Override
    public String resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return V.b_servers.get(ec);
    }
}
