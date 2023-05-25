package su.ptx.emka.junit.ctx;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.util.function.Supplier;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import su.ptx.emka.server.EmKaServer;

final class EmKaCtx implements Ctx {
  private final Supplier<Store> stor;

  EmKaCtx(ExtensionContext ec) {
    var ns = create("su.ptx.emka");
    stor = () -> ec.getStore(ns);
  }

  @Override
  public <T> T pass(T o) {
    return stor.get().getOrComputeIfAbsent(Acs.class).pass(o);
  }

  @Override
  public String b_servers() {
    return stor.get().getOrComputeIfAbsent(
      "b_servers",
      k -> pass(EmKaServer.create()).run().bootstrapServers(),
      String.class);
  }
}
