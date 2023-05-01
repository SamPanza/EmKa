package su.ptx.emka.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.function.IntSupplier;

final class FreePort implements IntSupplier {
    @Override
    public int getAsInt() {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
