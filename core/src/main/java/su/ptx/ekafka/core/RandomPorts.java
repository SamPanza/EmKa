package su.ptx.ekafka.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.stream.IntStream;

final class RandomPorts {
    private RandomPorts() {
    }

    static int randomPort() {
        try (var s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static int[] randomPorts(short n) {
        return IntStream.generate(RandomPorts::randomPort).limit(n).toArray();
    }
}
