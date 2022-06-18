package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGlossaryGenerator.DEFAULT_SAF_FILENAME;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors.GithubReader;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.ScopeRef;
import ltd.datasoc.labs.ctwg.mrg.model.Term;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sih
 */
@Slf4j
class ModelWrangler {

  private YamlWrangler yamlWrangler;
  private GithubReader reader;

  private static final String TERM_HORIZONTAL_RULE = "---";
  private static final String MARKDOWN_HEADING = "#";

  ModelWrangler(YamlWrangler yamlWrangler, GithubReader reader) {
    this.yamlWrangler = yamlWrangler;
    this.reader = reader;
  }

  ModelWrangler() {
    this(new YamlWrangler(), new GithubReader());
  }

  private static final String HTTPS = "https://";
  private static final String TREE = "tree";
  private static final String MRG_FILE_EXTENSION = "yaml";
  private static final int OWNER_PART_INDEX = 1;
  private static final int REPO_PART_INDEX = 2;

  private static final String MULTIPLE_USE_FIELDS = "# `Multiple-use fields` ";
  private static final String GENERIC_FRONT_MATTER = "generic front-matter";

  // TODO getAllTerms and create a filter for the terms


  SAFModel getSaf(String scopedir, String safFilename) throws MRGGenerationException {
    String safAsString = this.getSafAsString(scopedir, safFilename);
    SAFModel safModel = yamlWrangler.parseSaf(safAsString);
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

  void writeMrgToFile(MRGModel mrg, String rootDir, String glossaryDir, String mrgFilename)
      throws MRGGenerationException {
    Path mrgFilepath = Path.of(rootDir, glossaryDir, mrgFilename);
  }

  List<Term> fetchTerms(GeneratorContext localContext, String curatedDir) {
    List<Term> terms = new ArrayList<>();
    String curatedPath = String.join("/", localContext.getRootDirPath(), curatedDir);
    List<String> directoryContent =
        reader.getDirectoryContent(localContext.getOwnerRepo(), curatedPath);
    if (!directoryContent.isEmpty()) {
      terms =
          directoryContent.stream()
              .map(dirty -> this.cleanTermFile(dirty))
              .map(clean -> yamlWrangler.parseTerm(clean))
              .toList();
    }
    return terms;
  }

  private String cleanTermFile(String dirtyTermFile) {
    StringBuilder cleanYaml = new StringBuilder();
    String[] lines = dirtyTermFile.split("\n");
    boolean sectionOfInterest = false;
    for (String line : lines) {
      if (line.startsWith(MARKDOWN_HEADING)) {
        sectionOfInterest = isSectionOfInterest(line);
      }
      if (isClean(line) && sectionOfInterest) {
        cleanYaml.append(line);
        cleanYaml.append("\n");
      }
    }
    return cleanYaml.toString();
  }

  /*
    Used to filter out items in the file that aren't of interest to the MRG but more oriented
    towards human-readable content
  */
  private boolean isSectionOfInterest(String line) {
    return !StringUtils.isEmpty(line)
        && (line.startsWith(MULTIPLE_USE_FIELDS) || line.contains(GENERIC_FRONT_MATTER));
  }

  /*
    Used to filter out headings and other human-style clutter
  */
  private boolean isClean(String line) {
    return !StringUtils.isEmpty(line)
        && !line.startsWith(TERM_HORIZONTAL_RULE)
        && !line.startsWith(MARKDOWN_HEADING);
  }

  private GeneratorContext createSkeletonContext(String scopedir) {
    String ownerRepo = getOwnerRepo(scopedir);
    String rootPath = getRootPath(scopedir);
    return new GeneratorContext(ownerRepo, rootPath);
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
