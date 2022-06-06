package ltd.datasoc.labs.ctwg.mrg.model;

import java.util.List;
import lombok.Data;

/**
 * @author sih
 */
@Data
public class ScopeRef {
  private List<String> scopetags;
  private String scopedir;
}
