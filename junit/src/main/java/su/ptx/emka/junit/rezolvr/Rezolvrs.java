package su.ptx.emka.junit.rezolvr;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class Rezolvrs {
    private Rezolvrs() {
    }

    private static final Collection<Rezolvr<?>> rezolvrs = List.of(
            new BootstrapServersRezolvr(),
            new AdminRezolvr(),
            new ProducerRezolvr(),
            new ConsumerRezolvr());

    public static Stream<Rezolvr<?>> rezolvrs() {
        return rezolvrs.stream();
    }
}
