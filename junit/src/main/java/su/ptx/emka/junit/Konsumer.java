package su.ptx.emka.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
//TODO: Needs testing:
// 1) annotate test class field
// 2) annotate test method parameter
// 3) non-default group
// 4) non-default resetTo
public @interface Konsumer {
    String group() default "";

    ResetTo resetTo() default ResetTo.earliest;

    //https://kafka.apache.org/documentation/#consumerconfigs_auto.offset.reset
    //https://docs.confluent.io/platform/current/installation/configuration/consumer-configs.html#auto-offset-reset
    enum ResetTo {
        latest, earliest, none
    }
}
