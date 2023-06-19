package su.ptx.emka.app;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.junit.jupiter.api.Assertions.fail;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.register;
import static su.ptx.emka.app.Jmx.withConnection;
import static su.ptx.emka.aux.Pair.pair;
import static su.ptx.emka.test.AssThr.assThr;
import static su.ptx.emka.test.StringPredicates.any;
import static su.ptx.emka.test.StringPredicates.eq;

import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.naming.InvalidNameException;
import org.junit.jupiter.api.Test;
import su.ptx.emka.app.mbobz.Bar;

class JmxFailureTests {
  private static final MBeanServerConnection CONN = getPlatformMBeanServer();

  @Test
  void register_throwing_bean() {
    assThr(
        () -> register(new Bar()),
        pair(Jmx.E.class, any()),
        pair(MBeanRegistrationException.class, any()));
  }

  @Test
  void register_not_compliant_bean() {
    assThr(
        () -> register(new Object()),
        pair(Jmx.E.class, any()),
        pair(NotCompliantMBeanException.class, any()));
  }

  @Test
  void get_attr_of_unknown_instance() {
    assThr(
        () -> attr(CONN, Object.class, "Class"),
        pair(Jmx.E.class, any()),
        pair(InstanceNotFoundException.class, any()));
  }

  @Test
  void connect_to_bad_port() {
    assThr(
        () -> withConnection(-1, conn -> fail()),
        pair(Jmx.E.class, any()),
        pair(IOException.class, any()),
        pair(InvalidNameException.class, eq("invalid authority: 127.0.0.1:-1")));
  }
}
