package su.ptx.emka.aux;

import java.util.Map;

/**
 * TODO: javadoc.
 *
 * @param <F> former (first) type
 * @param <L> latter (last, second) type
 */
public final class Pair<F, L> {
  private final F f;
  private final L l;

  public static <F, L> Pair<F, L> pair(Map.Entry<F, L> e) {
    return new Pair<>(e.getKey(), e.getValue());
  }

  public static <F, L> Pair<F, L> pair(F former, L latter) {
    return new Pair<>(former, latter);
  }

  private Pair(F former, L latter) {
    f = former;
    l = latter;
  }

  public F f() {
    return f;
  }

  public L l() {
    return l;
  }
}
