package su.ptx.emka.junit;

import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

class FieldContext {
    private final Field field;
    private final Object instance;

    FieldContext(Field field, Object instance) {
        this.field = field;
        this.instance = instance;
    }

    Object get() {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void set(Object v) {
        try {
            field.set(instance, v);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Class<?> getType() {
        return field.getType();
    }

    <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
        return AnnotationSupport.findAnnotation(field, annotationType);
    }

    boolean isAnnotated(Class<? extends Annotation> annotationType) {
        return AnnotationSupport.isAnnotated(field, annotationType);
    }
}
