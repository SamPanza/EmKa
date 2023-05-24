package su.ptx.emka.junit.ctx;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

final class Acs implements CloseableResource {
  private final Deque<AutoCloseable> acs = new LinkedBlockingDeque<>();

  <T> T pass(T o) {
    if (o instanceof AutoCloseable ac) {
      acs.push(ac);
    }
    return o;
  }

  @Override
  public void close() {
    while (!acs.isEmpty()) {
      try {
        acs.pop().close();
      } catch (Exception e) {
        //
      }
    }
  }
}
