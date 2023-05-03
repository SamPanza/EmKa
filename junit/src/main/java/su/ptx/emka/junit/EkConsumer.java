package su.ptx.emka.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface EkConsumer {
    //TODO: Tests for non-default group
    String group() default "";

    //TODO: Tests for non-default autoOffsetReset
    AutoOffsetReset autoOffsetReset() default AutoOffsetReset.earliest;

    enum AutoOffsetReset {
        latest, earliest, none
    }
}
