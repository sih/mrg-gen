package ltd.datasoc.labs.ctwg.mrg.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

/**
 * @author sih
 */
@Slf4j
public class GithubReader implements MRGConnector {

  private static final String GH_NAME = "GH_NAME";

  private static final String GH_TOKEN = "GH_TOKEN";
  private GitHub gh;

  public GithubReader() {
    try {
      gh = GitHub.connect(System.getenv(GH_NAME), System.getenv(GH_TOKEN));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    }
  }

  @Override
  public String getContent(final String repository, final String contentName) {
    GHRepository repo = null;
    try {
      repo = gh.getRepository(repository);
      GHContent content = repo.getFileContent(contentName);
      if (null == content) {
        return null;
      }
      return contentAsString(content);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<FileContent> getDirectoryContent(
      final String repository, final String directoryName) {
    List<FileContent> contents = new ArrayList<>();
    try {
      GHRepository repo = gh.getRepository(repository);
      List<GHContent> gitContents = repo.getDirectoryContent(directoryName);
      if (gitContents != null && !gitContents.isEmpty()) {
        contents =
            gitContents.stream()
                .map(
                    gc ->
                        new FileContent(gc.getName(), this.contentAsString(gc), new ArrayList<>()))
                .toList();
      }
    } catch (GHFileNotFoundException e) {
      log.warn("There's no such directory {} in the repo {}", directoryName, repository);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return contents;
  }

  private String contentAsString(GHContent content) {
    try {
      InputStream is = content.read();
      return new String(is.readAllBytes(), StandardCharsets.US_ASCII);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
