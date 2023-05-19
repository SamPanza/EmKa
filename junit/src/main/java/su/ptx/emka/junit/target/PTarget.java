package su.ptx.emka.junit.target;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

final class PTarget extends ATarget {
  private final Parameter p;

  PTarget(Parameter p) {
    super(p);
    this.p = p;
  }

  @Override
  Class<?> type() {
    return p.getType();
  }

  @Override
  Type paraType() {
    return p.getParameterizedType();
  }
}
