package su.ptx.emka.app;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static su.ptx.emka.app.Assertions.assertJmxExceptionCause;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.objectName;
import static su.ptx.emka.app.Jmx.register;

import java.io.IOException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import su.ptx.emka.app.mbobz.Foo;

class FooJmxTests {
  private static final MBeanServerConnection CONN = getPlatformMBeanServer();

  @BeforeEach
  void setUp() {
    register(new Foo());
  }

  @AfterEach
  void tearDown() throws InstanceNotFoundException, MBeanRegistrationException, IOException {
    CONN.unregisterMBean(objectName(Foo.class));
  }

  @Test
  void register_already_existing_instance_throws() {
    assertJmxExceptionCause(InstanceAlreadyExistsException.class, () -> register(new Foo()));
  }

  @Test
  void get_unknown_attr_throws() {
    assertJmxExceptionCause(
        AttributeNotFoundException.class,
        () -> attr(CONN, Foo.class, "OhNo"));
  }

  @Test
  void get_throwing_attr_throws() {
    assertJmxExceptionCause(
        MBeanException.class,
        () -> attr(CONN, Foo.class, "Throw"));
  }
}
