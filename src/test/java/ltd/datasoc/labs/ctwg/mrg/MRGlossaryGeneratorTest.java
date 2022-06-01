package ltd.datasoc.labs.ctwg.mrg;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author sih
 */
class MRGlossaryGeneratorTest {

  @Test
  void simpleTest() {
    MRGlossaryGenerator mrg = new MRGlossaryGenerator();
    assertThat(mrg).isNotNull();
  }

}