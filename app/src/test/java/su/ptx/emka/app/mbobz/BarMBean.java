package su.ptx.emka.app.mbobz;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * TODO: javadoc.
 */
public interface BarMBean extends MBeanRegistration {
  @Override
  default ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
    return null;
  }

  @Override
  default void postRegister(Boolean registrationDone) {
  }

  @Override
  default void preDeregister() {
  }

  @Override
  default void postDeregister() {
  }
}
