package su.ptx.emka.app.mbobz;

/**
 * TODO: javadoc.
 */
public interface FooMBean {
  /**
   * TODO: javadoc.
   */
  default int getThrow() throws Oops {
    throw new Oops();
  }

  /**
   * TODO: javadoc.
   */
  final class Oops extends Exception {
    private Oops() {
      super("Oops!");
    }
  }
}
