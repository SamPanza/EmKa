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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

final class ProducerParamResolver implements EkParamResolver<EkProducer, Producer<?, ?>> {
    @Override
    public Class<EkProducer> annType() {
        return EkProducer.class;
    }

    @Override
    public Producer<?, ?> resolve(String b_servers, Annotation ann, Type[] atas) {
        return new KafkaProducer<>(
                Map.of(
                        "bootstrap.servers", b_servers,
                        "key.serializer", serializers().get(atas[0]),
                        "value.serializer", serializers().get(atas[1])));
    }

    private static Map<? extends Type, Class<? extends Serializer<?>>> serializers() {
        return ofEntries(
                entry(Short.class, ShortSerializer.class),
                entry(Double.class, DoubleSerializer.class),
                entry(byte[].class, ByteArraySerializer.class),
                entry(Integer.class, IntegerSerializer.class),
                entry(String.class, StringSerializer.class),
                entry(UUID.class, UUIDSerializer.class),
                entry(Void.class, VoidSerializer.class),
                entry(ByteBuffer.class, ByteBufferSerializer.class),
                entry(Float.class, FloatSerializer.class),
                entry(Long.class, LongSerializer.class),
                entry(Bytes.class, BytesSerializer.class));
    }
}
