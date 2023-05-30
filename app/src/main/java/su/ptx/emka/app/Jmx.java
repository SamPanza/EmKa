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

  private static ObjectName objectName(Object o) {
    return objectName(o.getClass());
  }

  static ObjectName objectName(Class<?> c) {
    try {
      return new ObjectName(c.getPackageName(), "type", c.getSimpleName());
    } catch (MalformedObjectNameException e) {
      throw new RuntimeException(e);
    }
  }

  static void register(Object o) {
    try {
      getPlatformMBeanServer().registerMBean(o, objectName(o));
    } catch (InstanceAlreadyExistsException
             | MBeanRegistrationException
             | NotCompliantMBeanException e) {
      throw new RuntimeException(e);
    }
  }

  static void withConnection(int port, Consumer<MBeanServerConnection> c) {
    try (var connector = connect(
        new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://127.0.0.1:%d/jmxrmi".formatted(port)))) {
      c.accept(connector.getMBeanServerConnection());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static <T> T attr(MBeanServerConnection c, ObjectName n, String a) {
    try {
      //noinspection unchecked
      return (T) c.getAttribute(n, a);
    } catch (AttributeNotFoundException
             | MBeanException
             | InstanceNotFoundException
             | ReflectionException
             | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
