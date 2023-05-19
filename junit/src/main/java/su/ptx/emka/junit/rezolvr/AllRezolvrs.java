package su.ptx.emka.junit.rezolvr;

import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static su.ptx.emka.junit.rezolvr.Rezolvr.rezolv;
import static su.ptx.emka.junit.rezolvr.Rezolvr.supports;

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
  public Optional<?> apply(Target t, Ctx c) {
    return rezolvrs.stream()
      .filter(supports(t))
      .findFirst()
      .map(rezolv(t, c))
      .map(c::pass);
  }

}
