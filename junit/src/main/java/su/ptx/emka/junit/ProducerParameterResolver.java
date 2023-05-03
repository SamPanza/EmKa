package su.ptx.emka.junit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.ByteBufferSerializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.FloatSerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.ShortSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static su.ptx.emka.junit.Ztore.b_servers;

final class ProducerParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: Look at type not annotation?
        return pc.isAnnotated(EkProducer.class);
    }

    @Override
    public Producer<?, ?> resolveParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: To utilities
        var atas = ((ParameterizedType) pc.getParameter().getParameterizedType()).getActualTypeArguments();
        return new KafkaProducer<>(Map.of(
                "bootstrap.servers", b_servers(ec),
                "key.serializer", serializers.get(atas[0]),
                "value.serializer", serializers.get(atas[1])));
    }

    private static final Map<? extends Type, Class<? extends Serializer<?>>> serializers = ofEntries(
            entry(byte[].class, ByteArraySerializer.class),
            entry(ByteBuffer.class, ByteBufferSerializer.class),
            entry(Bytes.class, BytesSerializer.class),
            entry(Double.class, DoubleSerializer.class),
            entry(Float.class, FloatSerializer.class),
            entry(Integer.class, IntegerSerializer.class),
            //NB: ListSerializer skipped
            entry(Long.class, LongSerializer.class),
            entry(Short.class, ShortSerializer.class),
            entry(String.class, StringSerializer.class),
            entry(UUID.class, UUIDSerializer.class),
            entry(Void.class, VoidSerializer.class));
}
