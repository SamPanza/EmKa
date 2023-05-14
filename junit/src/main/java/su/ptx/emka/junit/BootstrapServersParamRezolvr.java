package su.ptx.emka.junit;

final class BootstrapServersParamRezolvr implements ParamRezolvr {
    @Override
    public boolean supports(Target pc) {
        return pc.annotatedWith(BootstrapServers.class);
    }

    @Override
    public String resolve(Target pc, String b_servers) {
        return b_servers;
    }
}
