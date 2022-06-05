package ltd.datasoc.labs.ctwg.mrg;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Scope;
import ltd.datasoc.labs.ctwg.mrg.model.Scopes;
import ltd.datasoc.labs.ctwg.mrg.model.Versions;
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
  void given_fully_populated_saf_when_parse_then_populate_all_attrs() throws Exception {
    SAFModel actualModel = safParser.parse(SAF_SAMPLE_1_FILE);
    assertSAFSample1(actualModel);
  }

  /*
   scope:
     scopetag: "sample-1"
     scopedir: "./source"
     curatedir: "./curated"
     glossarydir: "./glossary"
     mrgfile: "mrg-sample-1.txt"
     hrgfile: "hrg-sample-1.txt"
     license: "Apache 2.0"
     statuses:
       - firstStatus
       - secondStatus
       - finalStatus
     issues: "https://https://github.com/sih/mrg-gen/issues"
     website: "https://https://github.com/sih/mrg-gen"
     slack: "mrg-gen"
     curators: "mailto:nemo@datasoc.ltd"
   scopes:
     scopetags:
       - "sample-1"
     scopedirs:
       - "./sources"
   versions:
     vsntag: "1.0.0"
     altvsntags:
       - v1
       - v1.0.0
     license: "Apache 2.0"
     termselcrit:
       - "current"
       - "latest"
     status: "prototype"
     from: "2022-06-05"
     to: "2022-12-05"
  */
  private void assertSAFSample1(SAFModel actualModel) {
    assertThat(actualModel).isNotNull();
    // scope
    Scope actualScope = actualModel.getScope();
    assertThat(actualScope).isNotNull();
    assertThat(actualScope.getScopetag()).isEqualTo("sample-1");
    assertThat(actualScope.getScopedir()).isEqualTo("./source");
    assertThat(actualScope.getCuratedir()).isEqualTo("./curated");
    assertThat(actualScope.getGlossarydir()).isEqualTo("./glossary");
    assertThat(actualScope.getMrgfile()).isEqualTo("mrg-sample-1.txt");
    assertThat(actualScope.getHrgfile()).isEqualTo("hrg-sample-1.txt");
    assertThat(actualScope.getLicense()).isEqualTo("Apache 2.0");
    assertThat(actualScope.getStatuses())
        .containsExactly("firstStatus", "secondStatus", "finalStatus");
    assertThat(actualScope.getIssues()).isEqualTo("https://https://github.com/sih/mrg-gen/issues");
    assertThat(actualScope.getWebsite()).isEqualTo("https://https://github.com/sih/mrg-gen");
    assertThat(actualScope.getSlack()).isEqualTo("mrg-gen");
    assertThat(actualScope.getCurators()).isEqualTo("mailto:nemo@datasoc.ltd");
    // scopes
    Scopes actualScopes = actualModel.getScopes();
    assertThat(actualScopes).isNotNull();
    assertThat(actualScopes.getScopetags()).containsExactly("sample-1");
    assertThat(actualScopes.getScopedirs()).containsExactly("./sources");
    // versions
    Versions actualVersions = actualModel.getVersions();
    assertThat(actualVersions).isNotNull();
    assertThat(actualVersions.getVsntag()).isEqualTo("1.0.0");
    assertThat(actualVersions.getAltvsntags()).containsExactly("v1", "v1.0.0");
    assertThat(actualVersions.getLicense()).isEqualTo("Apache 2.0");
    assertThat(actualVersions.getTermselcrit()).containsExactly("current", "latest");
    assertThat(actualVersions.getStatus()).isEqualTo("prototype");
    assertThat(actualVersions.getFrom()).isEqualTo("2022-06-05");
    assertThat(actualVersions.getTo()).isEqualTo("2022-12-05");
  }
}
