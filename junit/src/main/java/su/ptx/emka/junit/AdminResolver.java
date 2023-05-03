package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

final class AdminResolver implements EkResolver<EkAdmin, Admin> {
    @Override
    public Class<EkAdmin> aClass() {
        return EkAdmin.class;
    }

    @Override
    public Admin resolve(String b_servers, Annotation a, Type[] atas) {
        return Admin.create(Map.of(
                "bootstrap.servers", b_servers));
    }
}
