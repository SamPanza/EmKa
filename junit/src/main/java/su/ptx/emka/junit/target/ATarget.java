package su.ptx.emka.junit.target;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

//CHECKSTYLE-SUPPRESS: AbbreviationAsWordInName
abstract class ATarget implements Target {
  private final AnnotatedElement elem;

  ATarget(AnnotatedElement annotatedElement) {
    elem = annotatedElement;
  }

  @Override
  public final boolean assignableTo(Class<?> c) {
    return c.isAssignableFrom(type());
  }

  @Override
  public final boolean annotatedWith(Class<? extends Annotation> ac) {
    return isAnnotated(elem, ac);
  }

  @Override
  public final <A extends Annotation> Optional<A> find(Class<A> ac) {
    return findAnnotation(elem, ac);
  }

  @Override
  public final Type[] typeArgs() {
    return ((ParameterizedType) paraType()).getActualTypeArguments();
  }

  abstract Class<?> type();

  abstract Type paraType();
}
