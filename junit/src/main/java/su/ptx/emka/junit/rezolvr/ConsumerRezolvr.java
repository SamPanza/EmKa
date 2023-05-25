package su.ptx.emka.junit.rezolvr;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import su.ptx.emka.junit.Konsumer;
import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

final class ConsumerRezolvr implements Rezolvr<Consumer<?, ?>> {
  @Konsumer
  private static final boolean x = true;
  private static final Konsumer A;

  static {
    try {
      A = ConsumerRezolvr.class.getDeclaredField("x").getDeclaredAnnotation(Konsumer.class);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean test(Target t) {
    return t.annotatedWith(Konsumer.class) || t.assignableTo(Consumer.class);
  }

  @Override
  public Consumer<?, ?> apply(Target pc, Ctx c) {
    var a = pc.find(Konsumer.class).orElse(A);
    var typeArgs = pc.typeArgs();
    var kc = new KafkaConsumer<>(Map.of(
        "bootstrap.servers", c.b_servers(),
        "key.deserializer", deserializers.get(typeArgs[0]),
        "value.deserializer", deserializers.get(typeArgs[1]),
        "group.id", a.group().isBlank() ? "g_" + UUID.randomUUID() : a.group(),
        "auto.offset.reset", a.resetTo().name()));
    Optional.of(a)
        .map(Konsumer::subscribeTo)
        .filter(t -> !t.isEmpty())
        .map(Collections::singleton)
        .ifPresent(kc::subscribe);
    return kc;
  }

  private static final Map<? extends Type, Class<? extends Deserializer<?>>> deserializers =
      ofEntries(
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
