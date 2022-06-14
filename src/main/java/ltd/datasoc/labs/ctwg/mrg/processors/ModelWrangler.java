package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGlossaryGenerator.DEFAULT_MRG_FILENAME;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;

/**
 * @author sih
 */
class ModelWrangler {

  private SAFParser safParser;
  private GithubReader reader;

  @Getter(AccessLevel.PACKAGE)
  private final GeneratorContext generatorContext;

  ModelWrangler(GeneratorContext generatorContext) {
    this.generatorContext = generatorContext;
    safParser = new SAFParser();
    reader = new GithubReader();
  }

  private static final String HTTPS = "https://";
  private static final String TREE = "tree";
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
    String filePath = getFilepath(scopedir, safFilename);
    generatorContext.setSafFilepath(filePath);
    try {
      return reader.getContent(ownerRepo, filePath);

    } catch (Throwable t) {
      throw new MRGGenerationException(
          String.format(MRGGenerationException.NOT_FOUND, String.join("/", scopedir, safFilename)));
    }
  }

  MRGModel getMrg(String glossaryDir, String mrgFilename, String versionTag) {
    // TODO implement me
    String mrgVersionFilename = String.join(".", DEFAULT_MRG_FILENAME, versionTag, "json");
    String mrgFilepath = String.join("/", generatorContext.getRootDirPath(), mrgVersionFilename);
    generatorContext.setMrgFilepath(mrgFilepath);
    String mrgAsString = reader.getContent(generatorContext.getOwnerRepo(), mrgFilepath);

    return null;
  }

  private String getOwnerRepo(String scopedir) {
    int httpIndex = scopedir.indexOf(HTTPS) + HTTPS.length();
    int treeIndex = scopedir.indexOf(TREE) + TREE.length();
    String[] parts = scopedir.substring(httpIndex).split("/");
    String ownerRepo = String.join("/", parts[OWNER_PART_INDEX], parts[REPO_PART_INDEX]);
    generatorContext.setOwnerRepo(ownerRepo);
    return ownerRepo;
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
    generatorContext.setRootDirPath(path.substring(0, path.length() - 1));
    return path.append(safFilename).toString();
  }
}
