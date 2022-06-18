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

  public GeneratorContext(String ownerRepo, String rootDirPath) {
    this.ownerRepo = ownerRepo;
    this.rootDirPath = rootDirPath;
    this.safFilepath = String.join("/", rootDirPath, MRGlossaryGenerator.DEFAULT_SAF_FILENAME);
  }
}
