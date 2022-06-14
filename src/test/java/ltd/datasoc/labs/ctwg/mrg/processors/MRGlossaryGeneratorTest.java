package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_SUCH_VERSION;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Many of the requirements are taken from
 * https://essif-lab.github.io/framework/docs/tev2/tev2-toolbox#creating-an-mrg
 *
 * @author sih
 */
@ExtendWith(MockitoExtension.class)
class MRGlossaryGeneratorTest {
  @Mock private ModelWrangler mockWrangler;
  private MRGlossaryGenerator generator;
  private String scopedir;
  private String safFilename;
  private String version;
  private SAFModel noGlossarySaf;
  private SAFModel validSaf;
  private static final Path NO_GLOSSARY_SAF_PATH =
      Paths.get("./src/test/resources/no-glossary-saf.yaml");
  private static final Path VALID_SAF_PATH = Paths.get("./src/test/resources/saf-sample-1.yaml");

  @BeforeEach
  void set_up() throws Exception {
    SAFParser parser = new SAFParser();
    scopedir = "scopedir";
    safFilename = "saf.yaml";
    version = "version";
    generator = new MRGlossaryGenerator(mockWrangler);
    validSaf = parser.parse(new String(Files.readAllBytes(VALID_SAF_PATH)));
    noGlossarySaf = parser.parse(new String(Files.readAllBytes(NO_GLOSSARY_SAF_PATH)));
  }

  @Test
  @DisplayName("Should throw an exception when no glossary dir")
  void given_saf_with_no_glossary_dir_when_generate_then_throw_MRGException() throws Exception {
    when(mockWrangler.getSaf(scopedir, safFilename)).thenReturn(noGlossarySaf);
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> generator.generate(scopedir, safFilename, version))
        .withMessage(MRGGenerationException.NO_GLOSSARY_DIR);
  }

  @Test
  @DisplayName("Should throw an exception when no glossary dir")
  void given_saf_with_no_such_version_tag_when_generate_then_throw_MRGException() throws Exception {
    when(mockWrangler.getSaf(scopedir, safFilename)).thenReturn(validSaf);
    String badVersion = "moo";
    String expectedNoVersionMessage = String.format(NO_SUCH_VERSION, badVersion);
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> generator.generate(scopedir, safFilename, badVersion))
        .withMessage(expectedNoVersionMessage);
  }

  /*
   *
   */
  @DisplayName(
      "Given an MRG with an existing id; When generating from a term file with the same id; Then the existing glossary entry should be replaced by the newer")
  @Test
  void testMatchingIdsAreReplaced() {
    // TODO impelment me
  }

  @DisplayName(
      "Given more than one formphrases field; When generating an MRG; Then the formfields must be mutually exclusive wrt elements")
  @Test
  void testFormfieldsAreMutuallyExclusiveByElement() {}
}