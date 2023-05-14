package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;

import java.util.Map;

final class AdminParamRezolvr implements ParamRezolvr {
    @Override
    public boolean supports(ParamCtx pc) {
        return pc.assignableTo(Admin.class);
    }

    @Override
    public Object resolve(ParamCtx pc, String b_servers) {
        return Admin.create(Map.of(
                "bootstrap.servers", b_servers));
    }
}
