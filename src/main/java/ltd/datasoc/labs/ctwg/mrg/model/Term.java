package ltd.datasoc.labs.ctwg.mrg.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sih
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Term implements Comparable<Term> {
  @EqualsAndHashCode.Include private String id;
  private String scope;
  private String termtype;
  private String termid;
  private String formphrases;
  private String status;
  private String grouptags;
  private String created;
  private String updated;
  private String vsntag;
  private String commit;
  private String contributors;

  @Override
  public int compareTo(Term other) {
    return this.getId().compareTo(other.getId());
  }
}
