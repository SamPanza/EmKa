package su.ptx.emka.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface EkConsumer {
    String group() default "";

    AutoOffsetReset autoOffsetReset() default AutoOffsetReset.LATEST;

    enum AutoOffsetReset {
        LATEST, EARLIEST, NONE
    }
}
