package su.ptx.emka.junit.rezolvr;

import static su.ptx.emka.junit.rezolvr.Rezolvr.rezolv;
import static su.ptx.emka.junit.rezolvr.Rezolvr.supports;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import su.ptx.emka.junit.target.Target;

/**
 * TODO: javadoc.
 */
public final class AllRezolvrs implements Rezolvr<Optional<?>> {
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
  public Optional<?> apply(Target t, String b_servers) {
    return rezolvrs.stream()
      .filter(supports(t))
      .findFirst()
      .map(rezolv(t, b_servers));
  }
}
