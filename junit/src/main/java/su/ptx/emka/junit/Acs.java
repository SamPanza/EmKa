package su.ptx.emka.junit;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

final class Acs {
  private final Deque<AutoCloseable> acs = new LinkedBlockingDeque<>();

  <T> T pass(T o) {
    if (o instanceof AutoCloseable ac) {
      acs.push(ac);
    }
    return o;
  }

  void ssap() {
    while (!acs.isEmpty()) {
      try {
        acs.pop().close();
      } catch (Exception e) {
        //TODO: log
      }
    }
  }
}
