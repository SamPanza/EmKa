package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;

import static su.ptx.emka.junit.U.b_servers;

final class AdminParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: Look at type not annotation
        return pc.isAnnotated(EkAdmin.class);
    }

    @Override
    public Admin resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return Admin.create(Map.of(
                "bootstrap.servers", b_servers(ec)));
    }
}
