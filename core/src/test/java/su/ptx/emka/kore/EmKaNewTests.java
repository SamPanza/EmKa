package su.ptx.emka.kore;

import org.junit.jupiter.api.Test;
import su.ptx.emka.core.EmKa;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmKaNewTests {
    @Test
    void callBootstrapServersBeforeStart() throws Exception {
        try (var emKa = EmKa.create()) {
            //Broker's socket server is not ready yet, so NPE:
            // Cannot invoke "kafka.network.SocketServer.boundPort(org.apache.kafka.common.network.ListenerName)"
            // because the return value of "kafka.server.BrokerServer.socketServer()" is null
            // @ kafka.server.BrokerServer.boundPort(BrokerServer.scala:606)
            assertThrows(NullPointerException.class, emKa::bootstrapServers);
        }
    }

    @Test
    void callBootstrapServersAfterStart() throws Exception {
        try (var emKa = EmKa.create().start()) {
            assertTrue(emKa.bootstrapServers().matches("localhost:\\d\\d\\d\\d+"));
        }
    }
}
