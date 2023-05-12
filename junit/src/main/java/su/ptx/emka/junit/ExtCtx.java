package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import su.ptx.emka.core.EmKa;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

interface ExtCtx {
    void beforeEach();

    String b_servers();

    <T> T pass(T o);

    static ExtCtx of(ExtensionContext ec) {
        //TODO: Move Acs here
        final class Impl implements ExtCtx {
            private final ExtensionContext ec;

            private Impl(ExtensionContext ec) {
                this.ec = ec;
            }

            @Override
            public void beforeEach() {
                var acs = new Acs();
                ec.getStore(NS).put("acs", acs);
                var emKa = EmKa.create();
                acs.pass(emKa);
                emKa.start();
                ec.getStore(NS).put("b_servers", emKa.bootstrapServers());
            }

            @Override
            public String b_servers() {
                return ec.getStore(NS).get("b_servers", String.class);
            }

            @Override
            public <T> T pass(T o) {
                return ec.getStore(NS).get("acs", Acs.class).pass(o);
            }

            private static final ExtensionContext.Namespace NS = create("su.ptx.emka");
        }
        return new Impl(ec);
    }
}
