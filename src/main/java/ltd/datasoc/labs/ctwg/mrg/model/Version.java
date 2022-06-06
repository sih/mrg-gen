package ltd.datasoc.labs.ctwg.mrg.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author sih
 */
@Data
public final class Version {
  private String vsntag;
  private List<String> altvsntags;
  private List<String> terms;
  private String status;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyymmdd")
  private LocalDate from;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyymmdd")
  private LocalDate to;
}
