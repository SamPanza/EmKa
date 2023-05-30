package su.ptx.emka.junit.rezolvr;

import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import su.ptx.emka.junit.target.Target;

final class AdminRezolvr implements Rezolvr<Admin> {
  @Override
  public boolean test(Target t) {
    return t.assignableTo(Admin.class);
  }

  @Override
  public Admin apply(Target t, String b_servers) {
    return Admin.create(Map.of(
      "bootstrap.servers", b_servers));
  }
}
