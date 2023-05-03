package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

public final class EmKaExtension implements BeforeEachCallback, ParameterResolver {
    @Override
    public void beforeEach(ExtensionContext ec) throws Exception {
        //noinspection resource
        new EcV(
                ec,
                EmKa.create().start());
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pc.isAnnotated(Ek.class);
    }

    private static final EkResolver<?, ?>[] resolvers = {
            new BootstrapServersResolver(),
            new AdminResolver(),
            new ProducerResolver(),
            new ConsumerResolver()
    };

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        var ecV = EcV.get(ec);
        return ecV.count(
                stream(resolvers)
                        .filter(r -> pc.isAnnotated(r.aClass()))
                        .map(r -> r.resolve(
                                ecV.emKa.bootstrapServers(),
                                pc.findAnnotation(r.aClass()).orElseThrow(),
                                pc.getParameter().getParameterizedType() instanceof ParameterizedType pt
                                        ? pt.getActualTypeArguments()
                                        : null))
                        .findAny()
                        .orElse(null));
    }

    private static final class EcV implements CloseableResource {
        private final List<Object> counted = new ArrayList<>();
        private final EmKa emKa;

        private EcV(ExtensionContext ec, EmKa startedEmKa) {
            count(emKa = startedEmKa);
            ec.getStore(Namespace.GLOBAL).put(EcV.class, this);
        }

        private static EcV get(ExtensionContext ec) {
            return ec.getStore(Namespace.GLOBAL).get(EcV.class, EcV.class);
        }

        private Object count(Object o) {
            if (o != null) {
                counted.add(o);
            }
            return o;
        }

        @Override
        public void close() {
            for (var i = counted.size() - 1; i >= 0; --i) {
                if (counted.get(i) instanceof AutoCloseable ac) {
                    try {
                        ac.close();
                    } catch (Exception e) {
                        //NB: Silence?
                    }
                }
            }
        }
    }
}
