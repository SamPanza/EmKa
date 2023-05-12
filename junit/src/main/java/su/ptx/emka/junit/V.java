package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

final class V<T> {
    static final V<String> b_servers = new V<>("b_servers", String.class);
    static final V<Acs> acs = new V<>("acs", Acs.class);

    private final String key;
    private final Class<T> vClass;

    private V(String key, Class<T> vClass) {
        this.key = key;
        this.vClass = vClass;
    }

    private static final Namespace NS = create("su.ptx.emka");

    T get(ExtensionContext ec) {
        return ec.getStore(NS).get(key, vClass);
    }

    T put(ExtensionContext ec, T value) {
        ec.getStore(NS).put(key, value);
        return value;
    }
}
