package su.ptx.emka.junit.rezolvr;

import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Rezolvrs implements Rezolvr<Optional<?>> {
    private final Collection<Rezolvr<?>> rezolvrs = List.of(
            new BootstrapServersRezolvr(),
            new AdminRezolvr(),
            new ProducerRezolvr(),
            new ConsumerRezolvr());

    @Override
    public boolean test(Target t) {
        return rezolvrs.stream()
                .anyMatch(supports(t));
    }

    @Override
    public Optional<?> apply(Target t, Ctx c) {
        return rezolvrs.stream()
                .filter(supports(t))
                .findFirst()
                .map(rezolv(t, c))
                .map(c::pass);
    }

    private static Predicate<Rezolvr<?>> supports(Target t) {
        return r -> r.test(t);
    }

    private static Function<Rezolvr<?>, ?> rezolv(Target t, Ctx c) {
        return r -> r.apply(t, c);
    }
}
