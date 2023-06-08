package su.ptx.emka.clients;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.KafkaFuture;

/**
 * TODO: javadoc.
 */
public final class Kafut {
  private Kafut() {
  }

  /**
   * TODO: javadoc.
   */
  public static <T> T sneakyGet(KafkaFuture<T> f) {
    try {
      return f.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted", e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
