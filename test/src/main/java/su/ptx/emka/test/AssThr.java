package su.ptx.emka.test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import org.junit.jupiter.api.function.Executable;
import su.ptx.emka.aux.Pair;

/**
 * TODO: javadoc.
 */
public final class AssThr {
  private AssThr() {
  }

  /**
   * TODO: javadoc.
   */
  @SafeVarargs
  public static void assThr(Executable exe,
                            Pair<Class<? extends Exception>, Predicate<String>>... asses) {
    try {
      exe.execute();
    } catch (Throwable t) {
      var e = t;
      for (var ass : asses) {
        assertInstanceOf(ass.f(), e);
        assertTrue(ass.l().test(e.getMessage()));
        e = e.getCause();
      }
    }
  }
}
