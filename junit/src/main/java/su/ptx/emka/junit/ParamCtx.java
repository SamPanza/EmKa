package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

interface ParamCtx {
    boolean assignableTo(Class<?> c);

    boolean annotatedWith(Class<? extends Annotation> ac);

    <A extends Annotation> Optional<A> find(Class<A> ac);

    Type[] typeArgs();

    static ParamCtx of(ParameterContext pc) {
        final class Impl implements ParamCtx {
            private final ParameterContext pc;

            private Impl(ParameterContext pc) {
                this.pc = pc;
            }

            @Override
            public boolean assignableTo(Class<?> c) {
                return c.isAssignableFrom(pc.getParameter().getType());
            }

            @Override
            public boolean annotatedWith(Class<? extends Annotation> ac) {
                return pc.isAnnotated(ac);
            }

            @Override
            public <A extends Annotation> Optional<A> find(Class<A> ac) {
                return pc.findAnnotation(ac);
            }

            @Override
            public Type[] typeArgs() {
                return ((ParameterizedType) pc.getParameter().getParameterizedType()).getActualTypeArguments();
            }
        }
        return new Impl(pc);
    }
}
