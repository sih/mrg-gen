package ltd.datasoc.labs.ctwg.mrg.processors;

import lombok.Getter;

/**
 * @author sih
 */
@Getter
final class GeneratorContext {
  private String ownerRepo;
  private String rootDirPath;
  private String safFilepath;

  private String curatedDir;

  private String versionTag;

  public GeneratorContext(
      String ownerRepo, String rootDirPath, String versionTag, String curatedDir) {
    this.ownerRepo = ownerRepo;
    this.rootDirPath = rootDirPath;
    this.curatedDir = curatedDir;
    this.versionTag = versionTag;
    this.safFilepath = String.join("/", rootDirPath, MRGlossaryGenerator.DEFAULT_SAF_FILENAME);
  }
}
