package su.ptx.emka.junit.target;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

final class PTarget extends ATarget {
  private final Parameter param;

  PTarget(Parameter parameter) {
    super(parameter);
    param = parameter;
  }

  @Override
  Class<?> type() {
    return param.getType();
  }

  @Override
  Type paraType() {
    return param.getParameterizedType();
  }
}
