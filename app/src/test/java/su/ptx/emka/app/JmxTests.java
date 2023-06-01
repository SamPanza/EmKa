package su.ptx.emka.app;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static su.ptx.emka.app.Jmx.attr;
import static su.ptx.emka.app.Jmx.objectName;
import static su.ptx.emka.app.Jmx.register;
import static su.ptx.emka.app.Jmx.withConnection;

import java.io.IOException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class JmxTests {
  @BeforeEach
  void setUp() {
    register(new Foo());
  }

  @AfterEach
  void tearDown() throws InstanceNotFoundException, MBeanRegistrationException {
    getPlatformMBeanServer().unregisterMBean(objectName(Foo.class));
  }

  @Test
  void registerFooAgain() {
    assertJmxExceptionCause(InstanceAlreadyExistsException.class, () -> register(new Foo()));
  }

  @Test
  void registerThrowingBean() {
    assertJmxExceptionCause(MBeanRegistrationException.class, () -> register(new Bar()));
  }

  @Test
  void registerNotCompliantBean() {
    assertJmxExceptionCause(NotCompliantMBeanException.class, () -> register(new Object()));
  }

  @Test
  void connectToBadPort() {
    var e = assertJmxExceptionCause(IOException.class, () -> withConnection(-1, conn -> fail()));
    assertTrue(e.getCause().getMessage().startsWith("invalid authority: 127.0.0.1:-1"));
  }

  @Test
  void noSuchAttr() {
    assertJmxExceptionCause(
        AttributeNotFoundException.class,
        () -> attr(getPlatformMBeanServer(), objectName(Foo.class), "OhNo"));
  }

  private static <T extends Throwable> T assertJmxExceptionCause(Class<T> t, Executable e) {
    var jmxE = assertThrows(JmxException.class, e);
    var cause = jmxE.getCause();
    assertTrue(t.isInstance(cause), () -> "Expected %s got %s".formatted(t, cause.getClass()));
    return t.cast(cause);
  }

  public interface FooMBean {
  }

  private static final class Foo implements FooMBean {
  }

  public interface BarMBean {
  }

  private static final class Bar implements BarMBean, MBeanRegistration {
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
      throw new Exception();
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() {
    }

    @Override
    public void postDeregister() {
    }
  }
}
