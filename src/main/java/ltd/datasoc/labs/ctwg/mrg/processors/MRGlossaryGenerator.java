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
import ltd.datasoc.labs.ctwg.mrg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.connectors.LocalFSConnector;
import ltd.datasoc.labs.ctwg.mrg.connectors.MRGConnector;
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

  private ModelWrangler wrangler;

  @Setter(AccessLevel.PRIVATE)
  private Map<String, GeneratorContext> contextMap;

  public MRGlossaryGenerator() {
    this(true);
  }

  public MRGlossaryGenerator(boolean runLocal) {
    MRGConnector connector;
    if (runLocal) {
      connector = new LocalFSConnector();
    } else {
      connector = new GithubReader();
    }
    wrangler = new ModelWrangler(new YamlWrangler(), connector);
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
    // TODO
    //    entries.addAll(constructRemoteEntries(saf, localVersion));
    MRGModel mrg = new MRGModel(terminology, scopes, entries);
    wrangler.writeMrgToFile(mrg, mrgFilename);

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
    // TODO don't just get the first
    // e.g. [tev2]@tev2"
    // TODO use regex instead
    String unfilteredFilter = localVersion.getTerms().get(0).replace("[", "").replace("]", "");
    String filterTerm = unfilteredFilter.split("@")[0];
    List<Term> localTerms = wrangler.fetchTerms(localContext, filterTerm);
    return localTerms.stream().map(MRGEntry::new).toList();
  }

  /*
   Remote entries are selected from the mrg
  */
  private List<MRGEntry> constructRemoteEntries(SAFModel saf, Version localVersion) {
    return new ArrayList<>();
  }
}
