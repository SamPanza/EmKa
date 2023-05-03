package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static su.ptx.emka.junit.Ztore.Key.B_SERVERS;

interface Ztore {
    Namespace NS = create("su.ptx.emka");

    enum Key {
        ACS, B_SERVERS
    }

    default String b_servers(ExtensionContext ec) {
        return ec.getStore(NS).get(B_SERVERS, String.class);
    }
}
