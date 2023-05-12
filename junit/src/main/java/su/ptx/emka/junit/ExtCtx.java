package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import su.ptx.emka.core.EmKa;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

interface ExtCtx {
    void beforeEach();

    EmKa emKa();

    static ExtCtx of(ExtensionContext ec) {
        final class Impl implements ExtCtx {
            private final ExtensionContext ec;

            private Impl(ExtensionContext ec) {
                this.ec = ec;
            }

            private static final ExtensionContext.Namespace NS = create("su.ptx.emka");

            @Override
            public void beforeEach() {
                var emKa = EmKa.create();
                ec.getStore(NS).put("emKa", emKa);
                emKa.start();
            }

            @Override
            public EmKa emKa() {
                return ec.getStore(NS).get("emKa", EmKa.class);
            }
        }
        return new Impl(ec);
    }
}
