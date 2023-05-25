package su.ptx.emka.junit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * TODO: javadoc.
 */
@ExtendWith(EmKaExtension.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface EmKa {
}
