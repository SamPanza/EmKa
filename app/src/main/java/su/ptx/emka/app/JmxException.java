package su.ptx.emka.app;

import java.io.IOException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

final class JmxException extends RuntimeException {
  JmxException(InstanceAlreadyExistsException cause) {
    super(cause);
  }

  JmxException(MBeanRegistrationException cause) {
    super(cause);
  }

  JmxException(NotCompliantMBeanException cause) {
    super(cause);
  }

  JmxException(MalformedObjectNameException cause) {
    super(cause);
  }

  JmxException(IOException cause) {
    super(cause);
  }
}
