package ltd.datasoc.labs.ctwg.mrg.connectors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.COULD_NOT_READ_LOCAL_CONTENT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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

  private String getContent(Path contentPath) throws MRGGenerationException {
    String content;
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
    List<FileContent> contents;
    Path directoryPath = Path.of(repository, directoryName);
    try {
      Stream<Path> contentsAsPath = Files.walk(directoryPath);
      contents =
          contentsAsPath
              .map(
                  path ->
                      new FileContent(
                          path.getFileName().toString(), this.getContent(path), new ArrayList<>()))
              .toList();
    } catch (IOException ioe) {
      throw new MRGGenerationException(
          String.format(COULD_NOT_READ_LOCAL_CONTENT, directoryPath.toUri()));
    }
    return contents;
  }
}
