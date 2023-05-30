package su.ptx.emka.junit.target;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class FTarget extends ATarget {
  private final Field fld;

  FTarget(Field field) {
    super(field);
    fld = field;
  }

  @Override
  Class<?> type() {
    return fld.getType();
  }

  @Override
  Type paraType() {
    return fld.getGenericType();
  }
}
