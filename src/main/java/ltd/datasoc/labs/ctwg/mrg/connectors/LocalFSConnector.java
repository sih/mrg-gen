package ltd.datasoc.labs.ctwg.mrg.connectors;

import java.util.List;

/**
 * @author sih
 */
public class LocalFSConnector implements MRGConnector {

  @Override
  public String getContent(String repository, String contentName) {
    return null;
  }

  @Override
  public List<FileContent> getDirectoryContent(String repository, String directoryName) {
    return null;
  }
}
