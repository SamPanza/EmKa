package su.ptx.emka.junit.target;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

public interface Target {
    boolean assignableTo(Class<?> c);

    boolean annotatedWith(Class<? extends Annotation> ac);

    <A extends Annotation> Optional<A> find(Class<A> ac);

    Type[] typeArgs();

    static Target ofParameter(Parameter p) {
        return new PTarget(p);
    }

    static Target ofField(Field f) {
        return new FTarget(f);
    }
}
