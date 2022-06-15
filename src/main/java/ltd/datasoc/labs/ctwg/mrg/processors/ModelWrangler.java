package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGlossaryGenerator.DEFAULT_MRG_FILENAME;
import static ltd.datasoc.labs.ctwg.mrg.processors.MRGlossaryGenerator.DEFAULT_SAF_FILENAME;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.ScopeRef;

/**
 * @author sih
 */
class ModelWrangler {

  private SAFParser safParser;
  private GithubReader reader;

  ModelWrangler(SAFParser safParser, GithubReader reader) {
    this.safParser = safParser;
    this.reader = reader;
  }

  ModelWrangler() {
    this(new SAFParser(), new GithubReader());
  }

  private static final String HTTPS = "https://";
  private static final String TREE = "tree";
  private static final String MRG_FILE_EXTENSION = "yaml";
  private static final int OWNER_PART_INDEX = 1;
  private static final int REPO_PART_INDEX = 2;

  // TODO getAllTerms and create a filter for the terms



  SAFModel getSaf(String scopedir, String safFilename) throws MRGGenerationException {
    String safAsString = this.getSafAsString(scopedir, safFilename);
    SAFModel safModel = safParser.parse(safAsString);
    return safModel;
  }

  String getSafAsString(String scopedir, String safFilename) throws MRGGenerationException {
    String ownerRepo = getOwnerRepo(scopedir);
    String saf = (null == safFilename) ? DEFAULT_SAF_FILENAME : safFilename;
    String safFilepath = String.join("/", getRootPath(scopedir), saf);
    try {
      return reader.getContent(ownerRepo, safFilepath);

    } catch (Throwable t) {
      throw new MRGGenerationException(
          String.format(MRGGenerationException.NOT_FOUND, String.join("/", scopedir, safFilename)));
    }
  }

  Map<String, GeneratorContext> buildContextMap(SAFModel saf, String versionTag) {
    Map<String, GeneratorContext> contextMap = new HashMap<>();
    // do local scope
    String localScope = saf.getScope().getScopetag();
    String localScopedir = saf.getScope().getScopedir();
    GeneratorContext localContext = createSkeletonContext(localScopedir);
    contextMap.put(localScope, localContext);
    String mrgFilename = String.join(".", DEFAULT_MRG_FILENAME, versionTag, MRG_FILE_EXTENSION);
    // create skeleton external scopes
    List<ScopeRef> externalScopes = saf.getScopes();
    for (ScopeRef externalScope : externalScopes) {
      GeneratorContext generatorContext = createSkeletonContext(externalScope.getScopedir());
      List<String> scopetags = externalScope.getScopetags();
      for (String scopetag : scopetags) {
        contextMap.put(scopetag, generatorContext);
      }
    }

    return contextMap;
  }

  private GeneratorContext createSkeletonContext(String scopedir) {
    String ownerRepo = getOwnerRepo(scopedir);
    String rootPath = getRootPath(scopedir);
    return new GeneratorContext(ownerRepo, rootPath);
  }

  MRGModel getMrg(String glossaryDir, String mrgFilename, String versionTag) {
    // TODO implement me
    String mrgVersionFilename = String.join(".", DEFAULT_MRG_FILENAME, versionTag, "json");

    return null;
  }

  private String getOwnerRepo(String scopedir) {
    int httpIndex = scopedir.indexOf(HTTPS) + HTTPS.length();
    int treeIndex = scopedir.indexOf(TREE) + TREE.length();
    String[] parts = scopedir.substring(httpIndex).split("/");
    String ownerRepo = String.join("/", parts[OWNER_PART_INDEX], parts[REPO_PART_INDEX]);
    return ownerRepo;
  }

  private String getRootPath(String scopedir) {
    int treeIndex = scopedir.indexOf(TREE);
    if (treeIndex == -1) { // no tree found => root dir is /
      return "/";
    }
    treeIndex = treeIndex + +TREE.length() + 1; // step past "tree" itself
    String branchDir = scopedir.substring(treeIndex);
    String[] branchDirParts = branchDir.split("/");
    String[] dirParts = Arrays.copyOfRange(branchDirParts, 1, branchDirParts.length);
    StringBuilder path = new StringBuilder();
    for (int i = 0; i < dirParts.length; i++) {
      path.append(dirParts[i]).append("/");
    }
    return path.deleteCharAt(path.length() - 1).toString();
  }
}
