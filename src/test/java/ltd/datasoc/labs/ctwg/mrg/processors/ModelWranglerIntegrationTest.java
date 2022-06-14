package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGlossaryGenerator.DEFAULT_SAF_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Map;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class ModelWranglerIntegrationTest {

  private static final String TEV2_SCOPEDIR =
      "https://github.com/essif-lab/framework/tree/master/docs/tev2";
  private static final String PRIVATE_SCOPEDIR = "https://github.com/sih/scratch";
  private static final String SAF_FILENAME = "saf.yaml";
  private static final String MRGTEST_VERSION = "mrgtest";
  private String expectedOwnerRepo = "essif-lab/framework";
  private String expectedRootDirPath = "docs/tev2";
  private String expectedScopetag = "tev2";
  private String expectedSafFilepath = "docs/tev2/saf.yaml";

  private ModelWrangler wrangler;

  @BeforeEach
  void set_up() {
    wrangler = new ModelWrangler();
  }

  @Test
  void given_private_scopedir_when_get_saf_as_string_then_return_saf_exception() throws Exception {
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> wrangler.getSafAsString(PRIVATE_SCOPEDIR, SAF_FILENAME))
        .withMessage(
            String.format(
                MRGGenerationException.NOT_FOUND,
                String.join("", PRIVATE_SCOPEDIR, "/", DEFAULT_SAF_FILENAME)));
  }

  @Test
  void given_non_existent_saf_when_get_saf_as_string_then_return_saf_exception() throws Exception {
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> wrangler.getSafAsString(TEV2_SCOPEDIR, "foo"))
        .withMessage(
            String.format(
                MRGGenerationException.NOT_FOUND, String.join("", TEV2_SCOPEDIR, "/", "foo")));
  }

  @Test
  void given_saf_that_exists_when_get_saf_as_string_then_return_valid_content() throws Exception {
    String safString = wrangler.getSafAsString(TEV2_SCOPEDIR, SAF_FILENAME);
    assertThat(safString).isNotNull();
  }

  @Test
  void given_valid_saf_when_build_context_map_then_return_populated_map() throws Exception {
    SAFModel saf = wrangler.getSaf(TEV2_SCOPEDIR, SAF_FILENAME);
    Map<String, GeneratorContext> contextMap = wrangler.buildContextMap(saf, MRGTEST_VERSION);
    assertThat(contextMap).isNotEmpty();
    // check local context
    assertThat(contextMap).containsKey(expectedScopetag);
    GeneratorContext localContext = contextMap.get(expectedScopetag);
    assertThat(localContext.getOwnerRepo()).isEqualTo("essif-lab/framework");
    assertThat(localContext.getRootDirPath()).isEqualTo("docs/tev2");
    assertThat(localContext.getSafFilepath()).isEqualTo("docs/tev2/saf.yaml");
    // check external scopes
    assertThat(contextMap)
        .containsOnlyKeys(expectedScopetag, "essiflab", "essif-lab", "ctwg", "toip-ctwg");
    // essiflab
    GeneratorContext essiflabContext = contextMap.get("essiflab");
    assertThat(essiflabContext.getOwnerRepo()).isEqualTo("essif-lab/framework");
    assertThat(essiflabContext.getRootDirPath()).isEqualTo("docs");
    // essif-lab
    GeneratorContext essifLabContext = contextMap.get("essif-lab");
    assertThat(essifLabContext.getOwnerRepo()).isEqualTo("essif-lab/framework");
    assertThat(essifLabContext.getRootDirPath()).isEqualTo("docs");
    // ctwg
    GeneratorContext ctwgContext = contextMap.get("ctwg");
    assertThat(ctwgContext.getOwnerRepo()).isEqualTo("trustoverip/ctwg");
    assertThat(ctwgContext.getRootDirPath()).isEqualTo("/");
    // toip-ctwg
    GeneratorContext toipCtwgContext = contextMap.get("toip-ctwg");
    assertThat(toipCtwgContext.getOwnerRepo()).isEqualTo("trustoverip/ctwg");
    assertThat(toipCtwgContext.getRootDirPath()).isEqualTo("/");
  }
}
