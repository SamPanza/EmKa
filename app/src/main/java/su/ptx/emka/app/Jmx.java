package su.ptx.emka.app;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static javax.management.remote.JMXConnectorFactory.connect;

import java.io.IOException;
import java.util.function.Consumer;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXServiceURL;

final class Jmx {
  private Jmx() {
  }

  static void register(Object o) {
    try {
      getPlatformMBeanServer().registerMBean(o, objectName(o.getClass()));
    } catch (InstanceAlreadyExistsException e) {
      throw new E(e);
    } catch (MBeanRegistrationException e) {
      throw new E(e);
    } catch (NotCompliantMBeanException e) {
      throw new E(e);
    }
  }

  static ObjectName objectName(Class<?> c) {
    try {
      return new ObjectName(c.getPackageName(), "type", c.getSimpleName());
    } catch (MalformedObjectNameException e) {
      throw new RuntimeException(e);
    }
  }

  static void withConnection(int port, Consumer<MBeanServerConnection> c) {
    try (var connector = connect(
        new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://127.0.0.1:%d/jmxrmi".formatted(port)))) {
      c.accept(connector.getMBeanServerConnection());
    } catch (IOException e) {
      throw new E(e);
    }
  }

  static <T> T attr(MBeanServerConnection conn, Class<?> c, String a) {
    try {
      //noinspection unchecked
      return (T) conn.getAttribute(objectName(c), a);
    } catch (AttributeNotFoundException e) {
      throw new E(e);
    } catch (MBeanException e) {
      throw new E(e);
    } catch (InstanceNotFoundException e) {
      throw new E(e);
    } catch (ReflectionException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  static final class E extends RuntimeException {
    E(InstanceAlreadyExistsException cause) {
      super(cause);
    }

    E(MBeanRegistrationException cause) {
      super(cause);
    }

    E(NotCompliantMBeanException cause) {
      super(cause);
    }

    E(IOException cause) {
      super(cause);
    }

    E(AttributeNotFoundException cause) {
      super(cause);
    }

    E(MBeanException cause) {
      super(cause);
    }

    E(InstanceNotFoundException cause) {
      super(cause);
    }
  }
}
