package su.ptx.emka.junit;

import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;
import static su.ptx.emka.junit.target.Target.ofField;
import static su.ptx.emka.junit.target.Target.ofParameter;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import su.ptx.emka.junit.rezolvr.AllRezolvrs;
import su.ptx.emka.server.EmKaServer;

final class EmKaExtension
    implements
    BeforeEachCallback,
    AfterEachCallback,
    TestInstancePostProcessor,
    ParameterResolver {
  private final Acs acs = new Acs();
  private final EmKaServer server = EmKaServer.create();

  @Override
  public void beforeEach(ExtensionContext ec) {
    //
  }

  @Override
  public void afterEach(ExtensionContext ec) {
    acs.ssap();
  }

  private final AllRezolvrs allRezolvrs = new AllRezolvrs();

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext ec) {
    acs.pass(server.run());
    for (var f : findFields(testInstance.getClass(), f -> true, BOTTOM_UP)) {
      allRezolvrs.apply(ofField(f), server.bootstrapServers())
          .map(acs::pass)
          .ifPresent(v -> {
            f.setAccessible(true);
            try {
              f.set(testInstance, v);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          });
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
    return allRezolvrs.test(ofParameter(pc.getParameter()));
  }

  @Override
  public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
    return allRezolvrs.apply(
            ofParameter(pc.getParameter()),
            server.bootstrapServers())
        .map(acs::pass)
        .orElseThrow();
  }
}
