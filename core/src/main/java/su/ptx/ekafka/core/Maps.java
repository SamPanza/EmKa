package su.ptx.ekafka.core;

import scala.Tuple2;
import scala.collection.immutable.Map$;

import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static scala.jdk.javaapi.CollectionConverters.asScala;

final class Maps {
    private Maps() {
    }

    static <K, V>scala.collection.immutable.Map<K, V> empty() {
        return Map$.MODULE$.empty();
    }

    static <K, V> scala.collection.immutable.Map<K, V> map(Map<K, V> map) {
        return Map$.MODULE$.from(
                asScala(
                        map.entrySet().stream()
                                .map(entry ->
                                        new Tuple2<>(
                                                entry.getKey(),
                                                entry.getValue()))
                                .collect(toUnmodifiableSet())));
    }
}
