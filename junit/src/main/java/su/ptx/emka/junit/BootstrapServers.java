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
public @interface BootstrapServers {
}
