package ltd.datasoc.labs.ctwg.mrg;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sih
 */
@Getter
@Setter
public class MRGGenerationException extends Exception {
  private List<String> userMessages;

  public MRGGenerationException() {
    super();
  }

  public MRGGenerationException(List<String> userMessages) {
    this.userMessages = userMessages;
  }
}
