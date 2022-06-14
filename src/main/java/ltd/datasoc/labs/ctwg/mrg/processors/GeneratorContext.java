package ltd.datasoc.labs.ctwg.mrg.processors;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sih
 */
@Getter
@Setter
final class GeneratorContext {
  private String ownerRepo;
  private String rootDirPath;
  private String safFilepath;
  private String mrgFilepath;
}
