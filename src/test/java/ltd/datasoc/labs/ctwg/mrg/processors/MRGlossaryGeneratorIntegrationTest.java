package ltd.datasoc.labs.ctwg.mrg.processors;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class MRGlossaryGeneratorIntegrationTest {

  private MRGlossaryGenerator generator;
  private static final String SCOPEDIR =
      "https://github.com/essif-lab/framework/tree/master/docs/tev2";
  private static final String VERSION_TAG = "mrgtest";

  @BeforeEach
  void setUp() {
    generator = new MRGlossaryGenerator(false);
  }

  @Test
  @DisplayName("Should generate an MRG from the essif-lab/framework sample")
  void testGenerateTev2() throws Exception {
    MRGModel model =
        generator.generate(SCOPEDIR, MRGlossaryGenerator.DEFAULT_SAF_FILENAME, VERSION_TAG);
    assertThat(model).isNotNull();
    assertThat(model.terminology()).isNotNull();
    assertThat(model.scopes()).isNotNull();
    assertThat(model.entries()).isNotEmpty();
    File expectedMrg = new File(String.join(".", "mrg", VERSION_TAG, "yaml"));
    assertThat(expectedMrg).exists();
  }
}
