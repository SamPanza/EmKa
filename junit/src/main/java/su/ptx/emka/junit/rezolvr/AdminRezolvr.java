package su.ptx.emka.junit.rezolvr;

import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import su.ptx.emka.junit.ctx.Ctx;
import su.ptx.emka.junit.target.Target;

final class AdminRezolvr implements Rezolvr<Admin> {
  @Override
  public boolean test(Target t) {
    return t.assignableTo(Admin.class);
  }

  @Override
  public Admin apply(Target t, Ctx c) {
    return Admin.create(Map.of(
      "bootstrap.servers", c.b_servers()));
  }
}
