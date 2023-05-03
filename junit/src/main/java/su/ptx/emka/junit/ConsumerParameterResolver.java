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

final class ConsumerParameterResolver implements ParameterResolver, Ztore {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: Look at type AND annotation!
        return pc.isAnnotated(EkConsumer.class);
    }

    @Override
    public Consumer<?, ?> resolveParameter(ParameterContext pc, ExtensionContext ec) {
        //TODO: To utilities
        var atas = ((ParameterizedType) pc.getParameter().getParameterizedType()).getActualTypeArguments();
        var ekc = pc.findAnnotation(EkConsumer.class).orElseThrow();
        return new KafkaConsumer<>(Map.of(
                "bootstrap.servers", b_servers(ec),
                "key.deserializer", deserializers.get(atas[0]),
                "value.deserializer", deserializers.get(atas[1]),
                "group.id", ekc.group().isBlank() ? "g_" + UUID.randomUUID() : ekc.group(),
                "auto.offset.reset", ekc.autoOffsetReset().name().toLowerCase()));
    }

    private static final Map<? extends Type, Class<? extends Deserializer<?>>> deserializers = ofEntries(
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
