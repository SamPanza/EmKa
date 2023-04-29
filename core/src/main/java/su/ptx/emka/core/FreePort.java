package su.ptx.emka.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

final class FreePort {
    private FreePort() {
    }

    static int nextFreePort() {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
