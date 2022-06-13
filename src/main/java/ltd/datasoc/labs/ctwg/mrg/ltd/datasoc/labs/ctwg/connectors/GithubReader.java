package ltd.datasoc.labs.ctwg.mrg.ltd.datasoc.labs.ctwg.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

/**
 * @author sih
 */
public class GithubReader {

  private GitHub gh;

  public GithubReader() {
    try {
      gh = GitHub.connectAnonymously();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    }
  }

  public String getContent(final String repository, final String contentName) {
    try {
      GHRepository repo = gh.getRepository(repository);
      GHContent content = repo.getFileContent(contentName);
      if (null == content) {
        return null;
      }
      InputStream is = content.read();
      return new String(is.readAllBytes(), StandardCharsets.US_ASCII);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    try {
      GitHub gh = GitHub.connectAnonymously();
      GHRepository repo = gh.getRepository("essif-lab/framework");
      GHContent content = repo.getFileContent("README.md");
      InputStream is = content.read();
      String sContent = new String(is.readAllBytes(), StandardCharsets.US_ASCII);
      System.out.println(sContent);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
