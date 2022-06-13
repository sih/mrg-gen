package ltd.datasoc.labs.ctwg.mrg.generators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class ModelWranglerIntegrationTest {

  private static final String TEV2_SCOPEDIR =
      "https://github.com/essif-lab/framework/tree/master/docs/tev2";
  private static final String SAF_FILENAME = "saf.yaml";

  private ModelWrangler wrangler;

  @BeforeEach
  void set_up() {
    wrangler = new ModelWrangler();
  }

  @Test
  void given_saf_that_exists_when_get_saf_as_string_then_return_valid_content() throws Exception {
    String safString = wrangler.getSafAsString(TEV2_SCOPEDIR, SAF_FILENAME);
    assertThat(safString).isNotNull();
  }
}
