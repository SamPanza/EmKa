package su.ptx.emka.core;

record Host(String name) {
    WithPort withPort(int port) {
        return new WithPort(port);
    }

    final class WithPort {
        final int port;

        private WithPort(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return name + ":" + port;
        }
    }
}
