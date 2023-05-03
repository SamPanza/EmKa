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
// 4) non-default autoOffsetReset
public @interface EkConsumer {
    String group() default "";

    AutoOffsetReset autoOffsetReset() default AutoOffsetReset.earliest;

    enum AutoOffsetReset {
        latest, earliest, none
    }
}
