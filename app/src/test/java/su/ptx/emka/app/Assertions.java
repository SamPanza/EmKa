package su.ptx.emka.app;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;

final class Assertions {
  private Assertions() {
  }

  static <T extends Throwable> T assertJmxExceptionCause(Class<T> expectedCauseType,
                                                         Executable exe) {
    var jmxE = assertThrows(Jmx.E.class, exe);
    var cause = jmxE.getCause();
    assertTrue(
        expectedCauseType.isInstance(cause),
        () -> "Expected %s got %s".formatted(expectedCauseType, cause.getClass()));
    return expectedCauseType.cast(cause);
  }
}
