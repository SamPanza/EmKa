package su.ptx.emka.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

final class FreePorts implements IntSupplier, IntUnaryOperator {
    static final FreePorts FREE_PORTS = new FreePorts();

    private FreePorts() {
    }

    @Override
    public int getAsInt() {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int applyAsInt(int port) {
        return port == 0 ? getAsInt() : port;
    }
}
