package su.ptx.emka.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

final class BootstrapServersParamResolver implements EkParamResolver<EkBootstrapServers, String> {
    @Override
    public Class<EkBootstrapServers> annType() {
        return EkBootstrapServers.class;
    }

    @Override
    public String resolve(String b_servers, Annotation ann, Type[] atas) {
        return b_servers;
    }
}
