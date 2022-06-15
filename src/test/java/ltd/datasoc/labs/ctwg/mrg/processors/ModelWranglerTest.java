package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.UNABLE_TO_PARSE_SAF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author sih
 */
@ExtendWith(MockitoExtension.class)
class ModelWranglerTest {

  private static final String MRGTEST_VERSION = "mrgtest";
  @Mock private GithubReader mockReader;
  private SAFParser safParser = new SAFParser();
  private ModelWrangler wrangler;
  private static final Path INVALID_SAF = Paths.get("./src/test/resources/invalid-saf.yaml");
  private static final Path VALID_SAF = Paths.get("./src/test/resources/saf-sample-1.yaml");
  private static final String REPO = "https://github.com/essif-lab/framework/tree/master/docs/tev2";
  private static final String OWNER_REPO = "essif-lab/framework";
  private static final String VALID_SAF_NAME = "valid.saf";
  private static final String INVALID_SAF_NAME = "invalid.saf";
  private static final String VALID_SAF_TRIGGER = String.join("", "docs/tev2/", VALID_SAF_NAME);
  private static final String INVALID_SAF_TRIGGER = String.join("", "docs/tev2/", INVALID_SAF_NAME);
  private String invalidSafContent;
  private String validSafContent;

  @BeforeEach
  void set_up() throws Exception {
    wrangler = new ModelWrangler(safParser, mockReader);
    invalidSafContent = new String(Files.readAllBytes(INVALID_SAF));
    validSafContent = new String(Files.readAllBytes(VALID_SAF));
  }

  @Test
  void given_invalid_saf_when_get_saf_should_throw_exception() {
    when(mockReader.getContent(OWNER_REPO, INVALID_SAF_TRIGGER)).thenReturn(invalidSafContent);
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> wrangler.getSaf(REPO, INVALID_SAF_NAME))
        .withMessage(UNABLE_TO_PARSE_SAF);
  }

  @Test
  void given_valid_saf_when_get_saf_should_populate_key_fields() throws Exception {
    when(mockReader.getContent(OWNER_REPO, VALID_SAF_TRIGGER)).thenReturn(validSafContent);
    SAFModel saf = wrangler.getSaf(REPO, VALID_SAF_NAME);
    String expectedScopetag = "tev2";
    int expectedScopesCount = 2;
    int expectedVersionsCount = 3;
    assertThat(saf.getScope()).isNotNull();
    assertThat(saf.getScope().getScopetag()).isEqualTo(expectedScopetag);
    assertThat(saf.getScopes()).hasSize(expectedScopesCount);
    assertThat(saf.getVersions()).hasSize(expectedVersionsCount);
  }

  @Test
  void given_valid_saf_when_build_context_map_then_return_populated_map() throws Exception {
    when(mockReader.getContent(OWNER_REPO, VALID_SAF_TRIGGER)).thenReturn(validSafContent);
    String expectedScopetag = "tev2";
    SAFModel saf = wrangler.getSaf(REPO, VALID_SAF_NAME);
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
