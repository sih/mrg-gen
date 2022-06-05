package ltd.datasoc.labs.ctwg.mrg.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sih
 */
@Getter
@Setter
@EqualsAndHashCode
public class SAFModel {
  private Scope scope;
  private Scopes scopes;
  private Versions versions;
}
