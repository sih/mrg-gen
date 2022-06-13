package ltd.datasoc.labs.ctwg.mrg.generators;

import java.util.Arrays;
import ltd.datasoc.labs.ctwg.mrg.SAFParser;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Version;

/**
 * @author sih
 */
public class ModelWrangler {

  private SAFParser safParser;
  private GithubReader reader;

  public ModelWrangler() {
    safParser = new SAFParser();
    reader = new GithubReader();
  }

  private static final String DEFAULT_MRG_FILENAME = "mrg";
  private static final String DEFAULT_SAF_FILENAME = "saf.yaml";

  private static final String HTTPS = "https://";
  private static final String TREE = "tree";
  private static final int OWNER_PART_INDEX = 1;
  private static final int REPO_PART_INDEX = 2;

  // TODO getAllTerms and create a filter for the terms

  public String getSafAsString(String scopedir) {
    return getSafAsString(scopedir, DEFAULT_SAF_FILENAME);
  }

  public String getSafAsString(String scopedir, String safFilename) {
    String ownerRepo = getOwnerRepo(scopedir);
    String filePath = getFilepath(scopedir, safFilename);
    String content = reader.getContent(ownerRepo, filePath);
    return content;
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
