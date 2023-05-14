package su.ptx.emka.junit;

interface ParamRezolvr {
    boolean supports(Target pc);

    Object resolve(Target pc, String b_servers);
}
