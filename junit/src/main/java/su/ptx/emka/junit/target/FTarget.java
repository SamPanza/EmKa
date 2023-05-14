package su.ptx.emka.junit.target;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class FTarget extends ATarget {
    private final Field f;

    FTarget(Field f) {
        super(f);
        this.f = f;
    }

    @Override
    Class<?> type() {
        return f.getType();
    }

    @Override
    Type paraType() {
        return f.getGenericType();
    }
}
