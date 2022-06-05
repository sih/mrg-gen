package ltd.datasoc.labs.ctwg.mrg.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author sih
 */
@Data
public final class Versions {
  private String vsntag;
  private List<String> altvsntags;
  private String license;
  private List<String> termselcrit;
  private String status;
  private LocalDate from;
  private LocalDate to;
}
