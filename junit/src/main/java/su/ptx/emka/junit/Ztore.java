package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

final class Ztore {
    private Ztore() {
    }

    private static final Namespace NS = create("su.ptx.emka");

    private static Store store(ExtensionContext ec) {
        return ec.getStore(NS);
    }

    private enum Key {
        ACS, B_SERVERS
    }

    private static <V> V get(ExtensionContext ec, Key k, Class<V> vClass) {
        return store(ec).get(k, vClass);
    }

    private static <V> V put(ExtensionContext ec, Key k, V v) {
        store(ec).put(k, v);
        return v;
    }

    static String b_servers(ExtensionContext ec) {
        return get(ec, Key.B_SERVERS, String.class);
    }

    static String b_servers(ExtensionContext ec, String b_servers) {
        return put(ec, Key.B_SERVERS, b_servers);
    }

    static Acs acs(ExtensionContext ec) {
        return get(ec, Key.ACS, Acs.class);
    }

    static Acs acs(ExtensionContext ec, Acs acs) {
        return put(ec, Key.ACS, acs);
    }
}
