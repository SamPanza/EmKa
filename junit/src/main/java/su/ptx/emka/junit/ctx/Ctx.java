package su.ptx.emka.junit.ctx;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface Ctx {
  <T> T pass(T o);

  String b_servers();

  static Ctx ofExtensionContext(ExtensionContext ec) {
    return new EmKaCtx(ec);
  }
}
