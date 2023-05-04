package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;

import static su.ptx.emka.junit.V.B_SERVERS;

final class AdminParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return Admin.class.isAssignableFrom(pc.getParameter().getType());
    }

    @Override
    public Admin resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return Admin.create(Map.of(
                "bootstrap.servers", B_SERVERS.get(ec)));
    }
}
