package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_GLOSSARY_DIR;
import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.NO_SUCH_VERSION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ltd.datasoc.labs.ctwg.mrg.model.MRGEntry;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.ScopeRef;
import ltd.datasoc.labs.ctwg.mrg.model.Term;
import ltd.datasoc.labs.ctwg.mrg.model.Terminology;
import ltd.datasoc.labs.ctwg.mrg.model.Version;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sih
 */
@Slf4j
public class MRGlossaryGenerator {

  public static final String DEFAULT_MRG_FILENAME = "mrg";
  public static final String DEFAULT_SAF_FILENAME = "saf.yaml";

  private static final String TERMS_DIR = "./terms";

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
    String mrgFilename = constructFilename(localVersion);
    log.debug(String.format("MRG filename to be generated is: %s", mrgFilename));
    // construct the parts of the MRG Model
    Terminology terminology =
        new Terminology(saf.getScope().getScopetag(), saf.getScope().getScopedir());
    List<ScopeRef> scopes = new ArrayList<>(saf.getScopes());
    List<MRGEntry> entries =
        constructLocalEntries(contextMap.get(saf.getScope().getScopetag()), localVersion);
    entries.addAll(constructRemoteEntries(saf, localVersion));

    MRGModel mrg = new MRGModel(terminology, scopes, entries);
    wrangler.writeMrgToFile(mrg, ".", glossaryDir, mrgFilename);

    return mrg;
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

  private String constructFilename(Version localVersion) {
    // mrg.<versionTag>.yaml
    return String.join(".", DEFAULT_MRG_FILENAME, localVersion.getVsntag(), "yaml");
  }

  /*
   Local entries are selected from the curatedDir
  */
  private List<MRGEntry> constructLocalEntries(
      GeneratorContext localContext, Version localVersion) {
    List<Term> localTerms = wrangler.fetchTerms(localContext, localVersion.getVsntag());
    // TODO
    return new ArrayList<>();
  }

  /*
   Remote entries are selected from the mrg
  */
  private List<MRGEntry> constructRemoteEntries(SAFModel saf, Version localVersion) {
    return new ArrayList<>();
  }
}
