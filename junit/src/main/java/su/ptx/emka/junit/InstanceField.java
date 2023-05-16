package su.ptx.emka.junit;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

record InstanceField(Object o, Field f) implements Supplier<Object>, Consumer<Object> {
    @Override
    public Object get() {
        try {
            f.setAccessible(true);
            return f.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(Object v) {
        try {
            f.setAccessible(true);
            f.set(o, v);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
