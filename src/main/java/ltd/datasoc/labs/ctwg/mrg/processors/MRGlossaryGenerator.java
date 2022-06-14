package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_GLOSSARY_DIR;

import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sih
 */
public class MRGlossaryGenerator {

  public static final String DEFAULT_MRG_FILENAME = "mrg";
  public static final String DEFAULT_SAF_FILENAME = "saf.yaml";

  private ModelWrangler wrangler;
  private GeneratorContext generatorContext;

  public MRGlossaryGenerator(ModelWrangler wrangler) {
    this.wrangler = wrangler;
    this.generatorContext = wrangler.getGeneratorContext();
  }

  public MRGlossaryGenerator() {
    generatorContext = new GeneratorContext();
    this.wrangler = new ModelWrangler(generatorContext);
  }

  public MRGModel generate(final String scopedir, final String safFilename, final String version)
      throws MRGGenerationException {
    SAFModel saf = wrangler.getSaf(scopedir, safFilename);
    String glossaryDir = saf.getScope().getGlossarydir();
    if (StringUtils.isEmpty(glossaryDir)) {
      throw new MRGGenerationException(NO_GLOSSARY_DIR);
    }
    String mrgFile = saf.getScope().getMrgfile();

    return null;
  }
}
