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
        var v = EcV.get(ec);
        var bootstrapServers = v.emKa.bootstrapServers();
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
                                        "bootstrap.servers", bootstrapServers,
                                        "key.deserializer", KNOWN_DESERIALIZERS.get(atas[0]),
                                        "value.deserializer", KNOWN_DESERIALIZERS.get(atas[1]),
                                        "group.id", ((EkConsumer) ann).group().isBlank() ? "g_" + UUID.randomUUID() : ((EkConsumer) ann).group(),
                                        "auto.offset.reset", ((EkConsumer) ann).autoOffsetReset().name().toLowerCase())));
        Object pv = null;
        for (var e : m.entrySet()) {
            var k = e.getKey();
            if (pc.isAnnotated(k)) {
                var pr = e.getValue();
                pv = pr.rp(
                        bootstrapServers,
                        pc.getParameter().getParameterizedType() instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null,
                        pc.findAnnotation(k).orElseThrow());
                break;
            }
        }
        if (pv instanceof AutoCloseable ac) {
            v.toClose(ac);
        }
        return pv;
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

        private void toClose(AutoCloseable ac) {
            toClose.add(ac);
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
