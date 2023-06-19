package su.ptx.emka.test;

import java.util.function.Predicate;

/**
 * TODO: javadoc.
 */
public final class StringPredicates {
  private StringPredicates() {
  }

  public static Predicate<String> any() {
    return s -> true;
  }

  public static Predicate<String> eq(String that) {
    return s -> s.equals(that);
  }
}
