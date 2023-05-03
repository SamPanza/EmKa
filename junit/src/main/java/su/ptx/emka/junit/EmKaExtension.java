package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

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
        var b_servers = ecV.emKa.bootstrapServers();
        return ecV.count(
                stream(resolvers)
                        .filter(r -> pc.isAnnotated(r.aClass()))
                        .map(r -> r.resolve(b_servers, ann(pc, r.aClass()), atas(pc)))
                        .findAny()
                        .orElse(null));
    }

    private static Annotation ann(ParameterContext pc, Class<? extends Annotation> aClass) {
        return pc.findAnnotation(aClass).orElseThrow();
    }

    private static Type[] atas(ParameterContext pc) {
        return Optional.of(pc)
                .map(ParameterContext::getParameter)
                .map(Parameter::getParameterizedType)
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .map(ParameterizedType::getActualTypeArguments)
                .orElse(null);
    }

    private static final class EcV implements CloseableResource {
        private final Deque<AutoCloseable> acs = new ArrayDeque<>();
        private final EmKa emKa;

        private EcV(ExtensionContext ec, EmKa startedEmKa) {
            count(emKa = startedEmKa);
            ec.getStore(Namespace.GLOBAL).put(EcV.class, this);
        }

        private static EcV get(ExtensionContext ec) {
            return ec.getStore(Namespace.GLOBAL).get(EcV.class, EcV.class);
        }

        private Object count(Object o) {
            if (o instanceof AutoCloseable ac) {
                acs.push(ac);
            }
            return o;
        }

        @Override
        public void close() throws Exception {
            while (!acs.isEmpty()) {
                acs.pop().close();
            }
        }
    }
}
