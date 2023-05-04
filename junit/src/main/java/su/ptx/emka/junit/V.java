package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

final class V<T> {
    private static final Namespace EMKA = create("su.ptx.emka");
    static final V<String> B_SERVERS = new V<>(EMKA, "b_servers", String.class);
    static final V<Acs> ACS = new V<>(EMKA, "acs", Acs.class);
    private final Namespace ns;
    private final Object key;
    private final Class<T> vClass;

    private V(Namespace ns, Object key, Class<T> vClass) {
        this.ns = ns;
        this.key = key;
        this.vClass = vClass;
    }

    T get(ExtensionContext ec) {
        return ec.getStore(ns).get(key, vClass);
    }

    T put(ExtensionContext ec, T value) {
        ec.getStore(ns).put(key, value);
        return value;
    }
}
