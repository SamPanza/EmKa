package su.ptx.emka.junit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteBufferDeserializer;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.FloatDeserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.ShortDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.apache.kafka.common.utils.Bytes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

final class ConsumerParamResolver implements EkParamResolver<EkConsumer, Consumer<?, ?>> {
    @Override
    public Class<EkConsumer> aClass() {
        return EkConsumer.class;
    }

    @Override
    public Consumer<?, ?> resolve(String b_servers, Annotation a, Type[] atas) {
        var ekc = (EkConsumer) a;
        return new KafkaConsumer<>(Map.of(
                "bootstrap.servers", b_servers,
                "key.deserializer", deserializers().get(atas[0]),
                "value.deserializer", deserializers().get(atas[1]),
                "group.id", ekc.group().isBlank() ? "g_" + UUID.randomUUID() : ekc.group(),
                "auto.offset.reset", ekc.autoOffsetReset().name().toLowerCase()));
    }

    private static Map<? extends Type, Class<? extends Deserializer<?>>> deserializers() {
        return ofEntries(
                entry(byte[].class, ByteArrayDeserializer.class),
                entry(ByteBuffer.class, ByteBufferDeserializer.class),
                entry(Bytes.class, BytesDeserializer.class),
                entry(Double.class, DoubleDeserializer.class),
                entry(Float.class, FloatDeserializer.class),
                entry(Integer.class, IntegerDeserializer.class),
                //NB: ListDeserializer skipped
                entry(Long.class, LongDeserializer.class),
                entry(Short.class, ShortDeserializer.class),
                entry(String.class, StringDeserializer.class),
                entry(UUID.class, UUIDDeserializer.class),
                entry(Void.class, VoidDeserializer.class));
    }
}
