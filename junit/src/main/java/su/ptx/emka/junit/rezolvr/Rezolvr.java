package su.ptx.emka.junit.rezolvr;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

/**
 * TODO: javadoc.
 */
public interface Rezolvr<T> extends Predicate<Target>, BiFunction<Target, Ctx, T> {
  static Predicate<Rezolvr<?>> supports(Target t) {
    return r -> r.test(t);
  }

  static Function<Rezolvr<?>, ?> rezolv(Target t, Ctx c) {
    return r -> r.apply(t, c);
  }
}
