package su.ptx.emka.junit.rezolvr;

import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface Rezolvr<T> extends Predicate<Target>, BiFunction<Target, Ctx, T> {
}
