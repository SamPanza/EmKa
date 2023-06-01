package su.ptx.emka.app;

import java.io.IOException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
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

  JmxException(IOException cause) {
    super(cause);
  }

  JmxException(AttributeNotFoundException cause) {
    super(cause);
  }

  JmxException(MBeanException cause) {
    super(cause);
  }

  JmxException(InstanceNotFoundException cause) {
    super(cause);
  }
}
