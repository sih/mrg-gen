package ltd.datasoc.labs.ctwg.mrg.processors;

import java.util.Arrays;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Version;

/**
 * @author sih
 */
final class ModelWrangler {

  private SAFParser safParser;
  private GithubReader reader;

  ModelWrangler() {
    safParser = new SAFParser();
    reader = new GithubReader();
  }

  static final String DEFAULT_MRG_FILENAME = "mrg.json";
  static final String DEFAULT_SAF_FILENAME = "saf.yaml";

  private static final String HTTPS = "https://";
  private static final String TREE = "tree";
  private static final int OWNER_PART_INDEX = 1;
  private static final int REPO_PART_INDEX = 2;

  // TODO getAllTerms and create a filter for the terms

  String getSafAsString(String scopedir) throws MRGGenerationException {
    return getSafAsString(scopedir, DEFAULT_SAF_FILENAME);
  }

  String getSafAsString(String scopedir, String safFilename) throws MRGGenerationException {
    String ownerRepo = getOwnerRepo(scopedir);
    String filePath = getFilepath(scopedir, safFilename);
    try {
      return reader.getContent(ownerRepo, filePath);

    } catch (Throwable t) {
      throw new MRGGenerationException(
          String.format(MRGGenerationException.NOT_FOUND, String.join("/", scopedir, safFilename)));
    }
  }

  SAFModel getSaf(String scopedir) throws MRGGenerationException {
    return this.getSaf(scopedir, DEFAULT_SAF_FILENAME);
  }

  SAFModel getSaf(String scopedir, String safFilename) throws MRGGenerationException {
    String safAsString = this.getSafAsString(scopedir, safFilename);
    SAFModel safModel = safParser.parse(safAsString);
    return safModel;
  }

  Version getVersion(SAFModel saf, String versionTag) {
    // TODO implement me
    return null;
  }

  MRGModel getMrg(String glossaryDir, String versionTag) {
    // TODO implement me
    return getMrg(glossaryDir, DEFAULT_MRG_FILENAME, versionTag);
  }

  MRGModel getMrg(String glossaryDir, String mrgFilename, String versionTag) {
    // TODO implement me
    return null;
  }

  private String getOwnerRepo(String scopedir) {
    int httpIndex = scopedir.indexOf(HTTPS) + HTTPS.length();
    int treeIndex = scopedir.indexOf(TREE) + TREE.length();
    String[] parts = scopedir.substring(httpIndex).split("/");
    return String.join("/", parts[OWNER_PART_INDEX], parts[REPO_PART_INDEX]);
  }

  private String getFilepath(String scopedir, String safFilename) {
    int treeIndex = scopedir.indexOf(TREE) + TREE.length() + 1;
    String branchDir = scopedir.substring(treeIndex);
    String[] branchDirParts = branchDir.split("/");
    String[] dirParts = Arrays.copyOfRange(branchDirParts, 1, branchDirParts.length);
    StringBuilder path = new StringBuilder();
    for (int i = 0; i < dirParts.length; i++) {
      path.append(dirParts[i]).append("/");
    }
    return path.append(safFilename).toString();
  }
}
