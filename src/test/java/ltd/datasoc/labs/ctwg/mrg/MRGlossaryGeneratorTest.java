package ltd.datasoc.labs.ctwg.mrg;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Many of the requirements are taken from
 * https://essif-lab.github.io/framework/docs/tev2/tev2-toolbox#creating-an-mrg
 *
 * @author sih
 */
class MRGlossaryGeneratorTest {

  @Test
  void simpleTest() {
    MRGlossaryGenerator mrg = new MRGlossaryGenerator();
    assertThat(mrg).isNotNull();
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