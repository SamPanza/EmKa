package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Optional;

import static java.util.Arrays.stream;

final class DelegatingParamRezolvr implements ParameterResolver {
    private final ParamRezolvr[] rezolvrs;

    public DelegatingParamRezolvr(ParamRezolvr... rezolvrs) {
        this.rezolvrs = rezolvrs;
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return firstRezolvrSupporting(pc).isPresent();
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        //NB: or else throw?
        return firstRezolvrSupporting(pc)
                .map(r -> r.resolve(Target.of(pc), ExtCtx.of(ec).b_servers()))
                .orElse(null);
    }

    private Optional<ParamRezolvr> firstRezolvrSupporting(ParameterContext pc) {
        return stream(rezolvrs).filter(r -> r.supports(Target.of(pc))).findFirst();
    }
}
