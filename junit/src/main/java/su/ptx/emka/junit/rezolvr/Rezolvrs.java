package su.ptx.emka.junit.rezolvr;

import su.ptx.emka.junit.target.Target;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class Rezolvrs implements Rezolvr<Optional<?>> {
    private final Collection<Rezolvr<?>> rezolvrs = List.of(
            new BootstrapServersRezolvr(),
            new AdminRezolvr(),
            new ProducerRezolvr(),
            new ConsumerRezolvr());

    @Override
    public boolean test(Target t) {
        return rezolvrs.stream().anyMatch(r -> r.test(t));
    }

    @Override
    public Optional<?> apply(Target t, String b_servers) {
        return rezolvrs.stream().filter(r -> r.test(t)).map(r -> r.apply(t, b_servers)).findFirst();
    }
}
