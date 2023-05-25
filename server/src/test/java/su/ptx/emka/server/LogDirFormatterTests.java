package su.ptx.emka.server;

import static java.io.File.createTempFile;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static su.ptx.emka.server.LogDirFormatter.META_PROPS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogDirFormatterTests {
  @Test
  void format_random_tmp_dir() {
    assertTrue(format(null).isDirectory());
  }

  @Test
  void format_existing_empty_dir(@TempDir File dir) {
    assertSame(dir, format(dir));
    assertEquals(Set.of("bootstrap.checkpoint", META_PROPS), ls(dir));
  }

  @Test
  void format_not_existing_dir(@TempDir File parent) {
    assertDoesNotThrow(() -> format(new File(parent, "foo")));
  }

  @Test
  void format_already_formatted_dir(@TempDir File dir) throws IOException {
    assertTrue(new File(dir, META_PROPS).createNewFile());
    assertEquals(Set.of(META_PROPS), ls(format(dir)));
  }

  @Test
  void format_file() {
    var e = assertThrows(FileNotFoundException.class, () -> format(createTempFile("123", null)));
    assertTrue(e.getMessage().endsWith(".tmp/meta.properties.tmp (Not a directory)"));
  }

  @Test
  void make_mkdirs_fail() {
    var e = assertThrows(
        RuntimeException.class,
        () -> format(new File(createTempFile("123", null), "foo")));
    assertTrue(e.getMessage().startsWith("Can't create directory"));

  }

  private static File format(File dir) {
    return new LogDirFormatter(dir).format(1);
  }

  private static Set<String> ls(File dir) {
    return Set.of(requireNonNull(dir.list()));
  }
}
