package su.ptx.emka.junit.rezolvr;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import su.ptx.emka.junit.target.Target;

/**
 * TODO: javadoc.
 */
public interface Rezolvr<T> extends Predicate<Target>, BiFunction<Target, String, T> {
  static Predicate<Rezolvr<?>> supports(Target t) {
    return r -> r.test(t);
  }

  static Function<Rezolvr<?>, ?> rezolv(Target t, String b_servers) {
    return r -> r.apply(t, b_servers);
  }
}
