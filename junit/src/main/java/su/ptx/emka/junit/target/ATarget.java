package su.ptx.emka.junit.target;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

abstract class ATarget implements Target {
    private final AnnotatedElement a;

    ATarget(AnnotatedElement a) {
        this.a = a;
    }

    @Override
    public final boolean assignableTo(Class<?> c) {
        return c.isAssignableFrom(type());
    }

    @Override
    public final boolean annotatedWith(Class<? extends Annotation> ac) {
        return isAnnotated(a, ac);
    }

    @Override
    public final <A extends Annotation> Optional<A> find(Class<A> ac) {
        return findAnnotation(a, ac);
    }

    @Override
    public final Type[] typeArgs() {
        return ((ParameterizedType) paraType()).getActualTypeArguments();
    }

    abstract Class<?> type();

    abstract Type paraType();
}
