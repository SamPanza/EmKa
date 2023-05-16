package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ExtendWith(EmKaExtension.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface EmKa {
}
