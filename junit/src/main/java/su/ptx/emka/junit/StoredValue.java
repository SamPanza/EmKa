package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

final class StoredValue<V> {
    private static final Namespace EMKA = create("su.ptx.emka");

    static final StoredValue<String> B_SERVERS = new StoredValue<>(EMKA, "b_servers", String.class);
    static final StoredValue<Acs> ACS = new StoredValue<>(EMKA, "acs", Acs.class);

    private final Namespace ns;
    private final Object key;
    private final Class<V> vClass;

    private StoredValue(Namespace ns, Object key, Class<V> vClass) {
        this.ns = ns;
        this.key = key;
        this.vClass = vClass;
    }

    V get(ExtensionContext ec) {
        return ec.getStore(ns).get(key, vClass);
    }

    V put(ExtensionContext ec, V value) {
        ec.getStore(ns).put(key, value);
        return value;
    }
}
