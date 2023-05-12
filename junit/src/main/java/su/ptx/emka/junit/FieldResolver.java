package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;

interface FieldResolver {
    boolean supports(FieldContext fc, ExtensionContext ec);

    Object resolve(FieldContext fc, ExtensionContext ec);
}
