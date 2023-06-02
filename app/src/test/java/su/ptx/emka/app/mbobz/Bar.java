package su.ptx.emka.app.mbobz;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * TODO: javadoc.
 */
public final class Bar implements BarMBean {
  @Override
  public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
    throw new Exception();
  }
}
