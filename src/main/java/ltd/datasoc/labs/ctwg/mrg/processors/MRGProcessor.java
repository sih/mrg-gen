package ltd.datasoc.labs.ctwg.mrg.processors;

import ltd.datasoc.labs.ctwg.mrg.SAFParser;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Version;

/**
 * @author sih
 */
public class MRGProcessor {

  private SAFParser safParser;

  public MRGProcessor() {
    safParser = new SAFParser();
  }

  private static final String DEFAULT_MRG_FILENAME = "mrg";
  private static final String DEFAULT_SAF_FILENAME = "saf";

  // TODO getAllTerms and create a filter for the terms

  public String getSafAsString(String scopedir) {
    return getSafAsString(scopedir, DEFAULT_SAF_FILENAME);
  }

  public String getSafAsString(String scopedir, String safFilename) {
    // TODO implement me
    return null;
  }

  public Version getVersion(SAFModel saf, String versionTag) {
    // TODO implement me
    return null;
  }

  public MRGModel getMrg(String glossaryDir, String versionTag) {
    // TODO implement me
    return getMrg(glossaryDir, DEFAULT_MRG_FILENAME, versionTag);
  }

  public MRGModel getMrg(String glossaryDir, String mrgFilename, String versionTag) {
    // TODO implement me
    return null;
  }
}
