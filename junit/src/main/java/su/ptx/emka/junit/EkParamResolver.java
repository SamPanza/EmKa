package su.ptx.emka.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

interface EkParamResolver<A extends Annotation, T> {
    Class<A> aClass();

    T resolve(String b_servers, Annotation a, Type[] atas);
}
