package su.ptx.emka.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EmKaExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext ctx) {
        System.err.println("beforeEach");
    }

    @Override
    public void afterEach(ExtensionContext ctx) {
        System.err.println("afterEach");
    }
}
