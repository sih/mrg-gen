package ltd.datasoc.labs.ctwg.mrg.connectors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.COULD_NOT_READ_LOCAL_CONTENT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException;

/**
 * @author sih
 */
public class LocalFSConnector implements MRGConnector {

  @Override
  public String getContent(String repository, String contentName) {
    String content;
    Path contentPath = Path.of(repository, contentName);
    try {
      content = new String(Files.readAllBytes(contentPath));
    } catch (IOException e) {
      throw new MRGGenerationException(
          String.format(COULD_NOT_READ_LOCAL_CONTENT, contentPath.toUri()));
    }
    return content;
  }

  @Override
  public List<FileContent> getDirectoryContent(String repository, String directoryName) {
    return null;
  }
}
