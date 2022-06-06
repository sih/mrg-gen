package ltd.datasoc.labs.ctwg.mrg;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import ltd.datasoc.labs.ctwg.mrg.model.Curator;
import ltd.datasoc.labs.ctwg.mrg.model.Email;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Scope;
import ltd.datasoc.labs.ctwg.mrg.model.ScopeRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class SAFParserTest {
  private SAFParser safParser;
  private static final Path SAF_SAMPLE_1_FILE = Paths.get("./src/test/resources/saf-sample-1.yaml");
  @BeforeEach
  void setUp() {
    safParser = new SAFParser();
  }

  @Test
  void given_realistic_sample_with_when_parse_then_populate() throws Exception {
    SAFModel actualModel = safParser.parse(SAF_SAMPLE_1_FILE);
    assertThat(actualModel).isNotNull();
    assertThat(actualModel.getScope()).isNotNull();
    assertThat(actualModel.getScopes()).isNotEmpty();
    assertThat(actualModel.getVersions()).isNotEmpty();
    assertSpecificChecksForSample1(actualModel);
  }

  private void assertSpecificChecksForSample1(SAFModel actualModel) {
    LocalDate expectedDate = LocalDate.of(2022, 03, 12);

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

    List<ScopeRef> scopeRefs = actualModel.getScopes();
    int expectedNumberOfScopeRefs = 2;
    assertThat(scopeRefs.size()).isEqualTo(expectedNumberOfScopeRefs);
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
}
