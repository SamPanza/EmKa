package su.ptx.emka.app;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static su.ptx.emka.app.Assertions.assertJmxExceptionCause;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.register;
import static su.ptx.emka.app.Jmx.withConnection;

import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import org.junit.jupiter.api.Test;
import su.ptx.emka.app.mbobz.Bar;

class JmxFailureTests {
  private static final MBeanServerConnection CONN = getPlatformMBeanServer();

  @Test
  void register_throwing_bean() {
    assertJmxExceptionCause(
        MBeanRegistrationException.class,
        () -> register(new Bar()));
  }

  @Test
  void register_not_compliant_bean() {
    assertJmxExceptionCause(
        NotCompliantMBeanException.class,
        () -> register(new Object()));
  }

  @Test
  void get_attr_of_unknown_instance() {
    assertJmxExceptionCause(
        InstanceNotFoundException.class,
        () -> attr(CONN, Object.class, "Class"));
  }

  @Test
  void connect_to_bad_port() {
    var e = assertJmxExceptionCause(
        IOException.class,
        () ->
            withConnection(
                -1,
                conn ->
                    fail()));
    assertTrue(e.getCause().getMessage().startsWith("invalid authority: 127.0.0.1:-1"));
  }
}
