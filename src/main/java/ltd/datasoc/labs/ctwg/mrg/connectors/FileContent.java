package ltd.datasoc.labs.ctwg.mrg.connectors;

import java.util.List;

/**
 * @author sih
 */
public record FileContent(String filename, String content, List<String> headings) {

}
