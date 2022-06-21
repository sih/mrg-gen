package ltd.datasoc.labs.ctwg.mrg.connectors;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.FileSystems;
import ltd.datasoc.labs.ctwg.mrg.processors.GeneratorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class LocalFSConnectorTest {

  private GeneratorContext context;
  private static final String WORKING_DIRECTORY_KEY = "user.dir";
  private String scopedir;

  @BeforeEach
  void setUp() {
    String osSeparator = FileSystems.getDefault().getSeparator();
    // will use the SAF file in the ./src/test/resources directory so set this dir as the scopedir
    String projectRoot = System.getProperty(WORKING_DIRECTORY_KEY);
    scopedir = String.join(osSeparator, projectRoot, "src", "test", "resources");
    context = new GeneratorContext(scopedir, scopedir, "mrgtest", "terms");
  }

  @Test
  void checkScopedir() {
    assertThat(scopedir).isEqualTo("/Users/sid/dev/mrg-gen/src/test/resources");
  }
}
