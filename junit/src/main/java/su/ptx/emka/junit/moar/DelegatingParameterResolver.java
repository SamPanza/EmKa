package su.ptx.emka.junit.moar;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Optional;

import static java.util.Arrays.stream;

public class DelegatingParameterResolver implements ParameterResolver {
    private final ParameterResolver[] delegates;

    public DelegatingParameterResolver(ParameterResolver... delegates) {
        this.delegates = delegates;
    }

    @Override
    public final boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return d(pc, ec).isPresent();
    }

    @Override
    public final Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        //NB: or else throw?
        return d(pc, ec).map(r -> r.resolveParameter(pc, ec)).orElse(null);
    }

    private Optional<ParameterResolver> d(ParameterContext pc, ExtensionContext ec) {
        return stream(delegates).filter(r -> r.supportsParameter(pc, ec)).findFirst();
    }
}
