package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import su.ptx.emka.core.EmKa;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmKaExtension implements BeforeEachCallback, ParameterResolver {
    private static final Set<Class<? extends Annotation>> SUPPORTED = Set.of(
            EkBootstrapServers.class,
            EkAdmin.class,
            EkProducer.class,
            EkConsumer.class);
    //TODO: Other entries
    Map<? extends Type, Class<? extends Serializer<?>>> KNOWN_SERIALIZERS = Map.of(
            String.class, StringSerializer.class);
    //TODO: Other entries
    Map<? extends Type, Class<? extends Deserializer<?>>> KNOWN_DESERIALIZERS = Map.of(
            String.class, StringDeserializer.class);

    @Override
    public void beforeEach(ExtensionContext ec) throws Exception {
        EcV.setUp(ec);
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return SUPPORTED.stream().anyMatch(pc::isAnnotated);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: Refactor if if if if
        var v = EcV.get(ec);
        var bServers = v.emKa.bootstrapServers();
        if (pc.isAnnotated(EkBootstrapServers.class)) {
            return bServers;
        }
        if (pc.isAnnotated(EkAdmin.class)) {
            return v.toClose(Admin.create(Map.of("bootstrap.servers", bServers)));
        }
        if (pc.isAnnotated(EkProducer.class)) {
            var atas = ((ParameterizedType) pc.getParameter().getParameterizedType()).getActualTypeArguments();
            return v.toClose(new KafkaProducer<>(Map.of(
                    "bootstrap.servers", bServers,
                    "key.serializer", KNOWN_SERIALIZERS.get(atas[0]),
                    "value.serializer", KNOWN_SERIALIZERS.get(atas[1]))));
        }
        if (pc.isAnnotated(EkConsumer.class)) {
            //TODO
        }
        //TODO: null?
        return null;
    }

    private static final class EcV implements CloseableResource {
        private final EmKa emKa;
        private final List<AutoCloseable> toClose;

        private EcV() throws Exception {
            emKa = EmKa.create().start();
            toClose = new ArrayList<>();
            toClose.add(emKa);
        }

        private static void setUp(ExtensionContext ec) throws Exception {
            ec.getStore(Namespace.GLOBAL).put(EcV.class, new EcV());
        }

        private static EcV get(ExtensionContext ec) {
            return ec.getStore(Namespace.GLOBAL).get(EcV.class, EcV.class);
        }

        private AutoCloseable toClose(AutoCloseable ac) {
            toClose.add(ac);
            return ac;
        }

        @Override
        public void close() {
            for (var i = toClose.size() - 1; i >= 0; --i) {
                try {
                    toClose.get(i).close();
                } catch (Exception e) {
                    //TODO
                }
            }
        }
    }
}
