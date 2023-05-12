package su.ptx.emka.junit;

import org.apache.kafka.clients.admin.Admin;

import java.util.Map;

final class AdminParamRezolvr implements ParamRezolvr {
    @Override
    public boolean supports(ParamCtx pc) {
        return pc.assignableTo(Admin.class);
    }

    @Override
    public Object resolve(ParamCtx pc, ExtCtx ec) {
        return Admin.create(Map.of(
                "bootstrap.servers", ec.b_servers()));
    }
}
