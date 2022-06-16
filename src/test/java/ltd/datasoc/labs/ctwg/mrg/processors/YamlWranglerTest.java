package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.UNABLE_TO_PARSE_SAF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import ltd.datasoc.labs.ctwg.mrg.model.Curator;
import ltd.datasoc.labs.ctwg.mrg.model.Email;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Scope;
import ltd.datasoc.labs.ctwg.mrg.model.ScopeRef;
import ltd.datasoc.labs.ctwg.mrg.model.Version;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class YamlWranglerTest {
  private YamlWrangler yamlWrangler;
  private static final Path SAF_SAMPLE_1_FILE = Paths.get("./src/test/resources/saf-sample-1.yaml");
  private static final Path INVALID_YAML_FILE = Paths.get("./src/test/resources/invalid-saf.yaml");
  private String safAsString;
  private String invalidYamlSaf;

  @BeforeEach
  void setUp() throws Exception {
    yamlWrangler = new YamlWrangler();
    safAsString = new String(Files.readAllBytes(SAF_SAMPLE_1_FILE));
    invalidYamlSaf = new String(Files.readAllBytes(INVALID_YAML_FILE));
  }

  @Test
  void given_realistic_sample_with_when_parse_then_populate() throws Exception {
    SAFModel actualModel = yamlWrangler.parseSaf(safAsString);
    assertThat(actualModel).isNotNull();
    assertThat(actualModel.getScope()).isNotNull();
    assertThat(actualModel.getScopes()).isNotEmpty();
    assertThat(actualModel.getVersions()).isNotEmpty();
    assertSpecificChecksForSample1(actualModel);
  }

  @Test
  void given_invalid_yaml_when_parse_then_throw_MRGException() throws Exception {
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> yamlWrangler.parseSaf(invalidYamlSaf))
        .withMessage(UNABLE_TO_PARSE_SAF);
  }

  @Test
  void given_null_string_when_parse_then_throw_MRGException() throws Exception {
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> yamlWrangler.parseSaf(null))
        .withMessage(UNABLE_TO_PARSE_SAF);
  }

  @Test
  void given_empty_string_when_parse_then_throw_MRGException() throws Exception {
    assertThatExceptionOfType(MRGGenerationException.class)
        .isThrownBy(() -> yamlWrangler.parseSaf(StringUtils.EMPTY))
        .withMessage(UNABLE_TO_PARSE_SAF);
  }

  private void assertSpecificChecksForSample1(SAFModel actualModel) {
    // check top-level scope
    Scope actualTerminology = actualModel.getScope();
    Curator[] expectedCurators = {new Curator("RieksJ", new Email("rieks.joosten", "tno.nl"))};
    assertTerminology(
        actualTerminology,
        "tev2",
        "https://github.com/essif-lab/framework/tree/master/docs/tev2",
        "LICENSE.md",
        "https://github.com/essif-lab/framework/issues",
        "https://essif-lab.github.io/framework/docs/tev2/tev2-overview",
        "https://trustoverip.slack.com/archives/C01BBNGRPUH",
        expectedCurators);
    // check each of the scope references in the scopes element
    List<ScopeRef> scopeRefs = actualModel.getScopes();
    int expectedNumberOfScopeRefs = 2;
    assertThat(scopeRefs).hasSize(expectedNumberOfScopeRefs);
    ScopeRef expectedFirstScopeRef =
        new ScopeRef(
            List.of("essiflab", "essif-lab"),
            "https://github.com/essif-lab/framework/tree/master/docs");
    ScopeRef expectedSecondScopeRef =
        new ScopeRef(List.of("ctwg", "toip-ctwg"), "https://github.com/trustoverip/ctwg");
    ScopeRef[] actualScopes = scopeRefs.toArray(new ScopeRef[0]);
    for (int i = 0; i < actualScopes.length; i++) {
      assertScopeRef(actualScopes[0], expectedFirstScopeRef);
      assertScopeRef(actualScopes[1], expectedSecondScopeRef);
    }
    // check each of the versions
    List<Version> versions = actualModel.getVersions();
    int expectedNumberOfVersions = 3;
    assertThat(versions).hasSize(expectedNumberOfVersions);
    Version expectedFirstVersion =
        new Version("mrgtest", null, List.of("[tev2]@tev2"), null, null, null);
    Version expectedSecondVersion =
        new Version(
            "0x921456",
            List.of("latest", "v0.9.4"),
            List.of(
                "[management]@essif-lab",
                "[party](@essif-lab:0.9.4)",
                "[community](@essif-lab:0.9.4)",
                "[tev2]@tev2"),
            "proposed",
            "20220312",
            null);
    Version expectedThirdVersion =
        new Version(
            "0x654129",
            List.of("v0.9.0"),
            List.of(
                "[management]@essif-lab",
                "[party](@essif-lab:0.9.4)",
                "[community](@essif-lab:0.9.4)"),
            null,
            null,
            null);

    Version[] actualVersions = versions.toArray(new Version[0]);
    assertVersion(actualVersions[0], expectedFirstVersion);
    assertVersion(actualVersions[1], expectedSecondVersion);
  }

  void assertTerminology(
      Scope actualTerminology,
      String expectedScopetag,
      String expectedScopedir,
      String expectedLicense,
      String expectedIssues,
      String expectedWebsite,
      String expectedSlack,
      Curator[] expectedCurators) {
    assertThat(actualTerminology.getScopetag()).isEqualTo(expectedScopetag);
    assertThat(actualTerminology.getScopedir()).isEqualTo(expectedScopedir);
    assertThat(actualTerminology.getLicense()).isEqualTo(expectedLicense);
    assertThat(actualTerminology.getIssues()).isEqualTo(expectedIssues);
    assertThat(actualTerminology.getWebsite()).isEqualTo(expectedWebsite);
    assertThat(actualTerminology.getSlack()).isEqualTo(expectedSlack);
    assertThat(actualTerminology.getCurators()).containsExactly(expectedCurators);
  }

  private void assertScopeRef(ScopeRef actualScopeRef, ScopeRef expectedScoperef) {
    assertThat(actualScopeRef.getScopetags())
        .containsExactly(expectedScoperef.getScopetags().toArray(new String[0]));
    assertThat(actualScopeRef.getScopedir()).isEqualTo(expectedScoperef.getScopedir());
  }

  private void assertVersion(Version actualVersion, Version expectedVersion) {
    assertThat(actualVersion.getVsntag()).isEqualTo(expectedVersion.getVsntag());
    assertThat(actualVersion.getAltvsntags()).isEqualTo(expectedVersion.getAltvsntags());
    assertThat(actualVersion.getTerms())
        .containsExactly(expectedVersion.getTerms().toArray(new String[0]));
    assertThat(actualVersion.getStatus()).isEqualTo(expectedVersion.getStatus());
    assertThat(actualVersion.getFrom()).isEqualTo(expectedVersion.getFrom());
    assertThat(actualVersion.getTo()).isEqualTo(expectedVersion.getTo());
  }
}
