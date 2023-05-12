package su.ptx.emka.junit;

final class BootstrapServersParamRezolvr implements ParamRezolvr {
    @Override
    public boolean supports(ParamCtx pc) {
        return pc.annotatedWith(BootstrapServers.class);
    }

    @Override
    public String resolve(ParamCtx pc, ExtCtx ec) {
        return ec.emKa().bootstrapServers();
    }
}
