package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_GLOSSARY_DIR;
import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_SUCH_VERSION;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Setter;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Version;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sih
 */
public class MRGlossaryGenerator {

  public static final String DEFAULT_MRG_FILENAME = "mrg";
  public static final String DEFAULT_SAF_FILENAME = "saf.yaml";

  private ModelWrangler wrangler;

  @Setter(AccessLevel.PRIVATE)
  private Map<String, GeneratorContext> contextMap;

  public MRGlossaryGenerator() {
    this(new ModelWrangler());
  }

  public MRGlossaryGenerator(ModelWrangler wrangler) {
    this.wrangler = wrangler;
  }

  public MRGModel generate(final String scopedir, final String safFilename, final String versionTag)
      throws MRGGenerationException {
    SAFModel saf = wrangler.getSaf(scopedir, safFilename);
    this.setContextMap(wrangler.buildContextMap(saf, versionTag));
    String glossaryDir = saf.getScope().getGlossarydir();
    if (StringUtils.isEmpty(glossaryDir)) {
      throw new MRGGenerationException(NO_GLOSSARY_DIR);
    }
    Version localVersion = getVersion(saf, versionTag);

    // TODO handle version, e.g. "[party](@essif-lab:0.9.4)"

    return null;
  }

  private Version getVersion(SAFModel saf, String versionTag) throws MRGGenerationException {
    List<Version> versions = saf.getVersions();
    Optional<Version> version = Optional.empty();
    for (Version v : versions) {
      if (v.getVsntag().equals(versionTag)) {
        version = Optional.of(v);
        break;
      }
    }
    return version.orElseThrow(
        () -> new MRGGenerationException(String.format(NO_SUCH_VERSION, versionTag)));
  }
}
