package su.ptx.emka.junit;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * TODO: javadoc.
 */
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Konsumer {
  /**
   * TODO: javadoc.
   */
  String group() default "";

  /**
   * TODO: javadoc.
   */
  ResetTo resetTo() default ResetTo.earliest;

  /**
   * TODO: javadoc.
   */
  String subscribeTo() default "";

  //https://kafka.apache.org/documentation/#consumerconfigs_auto.offset.reset
  //https://docs.confluent.io/platform/current/installation/configuration/consumer-configs.html#auto-offset-reset
  /**
   * TODO: javadoc.
   */
  enum ResetTo {
    latest, earliest, none
  }
}
