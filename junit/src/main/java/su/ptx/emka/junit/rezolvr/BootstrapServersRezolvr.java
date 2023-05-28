package su.ptx.emka.junit.rezolvr;

import su.ptx.emka.junit.BootstrapServers;
import su.ptx.emka.junit.target.Target;

final class BootstrapServersRezolvr implements Rezolvr<String> {
  @Override
  public boolean test(Target t) {
    return t.annotatedWith(BootstrapServers.class);
  }

  @Override
  public String apply(Target t, String bservers) {
    return bservers;
  }
}
