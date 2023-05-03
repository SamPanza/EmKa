package su.ptx.emka.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

final class BootstrapServersResolver implements EkResolver<EkBootstrapServers, String> {
    @Override
    public Class<EkBootstrapServers> aClass() {
        return EkBootstrapServers.class;
    }

    @Override
    public String resolve(String b_servers, Annotation a, Type[] atas) {
        return b_servers;
    }
}
