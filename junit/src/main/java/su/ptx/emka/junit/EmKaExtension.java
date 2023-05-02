package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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
import java.util.UUID;

//TODO: Cleanup
public class EmKaExtension implements BeforeEachCallback, ParameterResolver {
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
        return pc.isAnnotated(Ek.class);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        @FunctionalInterface
        interface PR {
            Object rp(String bServers, Type[] atas, Annotation ann);
        }
        Map<Class<? extends Annotation>, PR> m = Map.of(
                EkBootstrapServers.class,
                (b_servers, atas, ann) ->
                        b_servers,
                EkAdmin.class,
                (b_servers, atas, ann) ->
                        Admin.create(Map.of("bootstrap.servers", b_servers)),
                EkProducer.class,
                (b_servers, atas, ann) ->
                        new KafkaProducer<>(
                                Map.of(
                                        "bootstrap.servers", b_servers,
                                        "key.serializer", KNOWN_SERIALIZERS.get(atas[0]),
                                        "value.serializer", KNOWN_SERIALIZERS.get(atas[1]))),
                EkConsumer.class,
                (b_servers, atas, ann) ->
                        new KafkaConsumer<>(
                                Map.of(
                                        "bootstrap.servers", b_servers,
                                        "key.deserializer", KNOWN_DESERIALIZERS.get(atas[0]),
                                        "value.deserializer", KNOWN_DESERIALIZERS.get(atas[1]),
                                        "group.id", ((EkConsumer) ann).group().isBlank() ? "g_" + UUID.randomUUID() : ((EkConsumer) ann).group(),
                                        "auto.offset.reset", ((EkConsumer) ann).autoOffsetReset().name().toLowerCase())));
        var ecV = EcV.get(ec);
        //TODO: Streamify
        Object v = null;
        for (var e : m.entrySet()) {
            var annType = e.getKey();
            if (pc.isAnnotated(annType)) {
                var pr = e.getValue();
                v = pr.rp(
                        ecV.emKa.bootstrapServers(),
                        pc.getParameter().getParameterizedType() instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null,
                        pc.findAnnotation(annType).orElseThrow());
                break;
            }
        }
        return ecV.count(v);
    }

    private static final class EcV implements CloseableResource {
        private final List<Object> counted;
        private final EmKa emKa;

        private EcV() throws Exception {
            counted = new ArrayList<>();
            counted.add(emKa = EmKa.create().start());
        }

        private static void setUp(ExtensionContext ec) throws Exception {
            ec.getStore(Namespace.GLOBAL).put(EcV.class, new EcV());
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
                        //TODO
                    }
                }
            }
        }
    }
}
