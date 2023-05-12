package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;

//TODO: Move V & Acs functionality here
interface ExtCtx {
    String b_servers();

    static ExtCtx of(ExtensionContext ec) {
        final class Impl implements ExtCtx {
            private final ExtensionContext ec;

            private Impl(ExtensionContext ec) {
                this.ec = ec;
            }

            @Override
            public String b_servers() {
                return V.b_servers.get(ec);
            }
        }
        return new Impl(ec);
    }
}
