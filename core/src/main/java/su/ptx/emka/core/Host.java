package su.ptx.emka.core;

final class Host {
    static final Host local = new Host("localhost");
    final String name;

    private Host(String name) {
        this.name = name;
    }

    String withPort(int port) {
        return name + ":" + port;
    }
}
