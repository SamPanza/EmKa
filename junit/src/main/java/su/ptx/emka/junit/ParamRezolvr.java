package su.ptx.emka.junit;

interface ParamRezolvr {
    boolean supports(ParamCtx pc);

    Object resolve(ParamCtx pc, ExtCtx ec);
}
