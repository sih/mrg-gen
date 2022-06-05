package ltd.datasoc.labs.ctwg.mrg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.nio.file.Path;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;

/**
 * @author sih
 */
public class SAFParser {

  private final ObjectMapper yamlMapper;

  public SAFParser() {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.findAndRegisterModules();
  }

  public SAFModel parse(Path safFile) throws Exception {
    return yamlMapper.readValue(safFile.toUri().toURL(), SAFModel.class);
  }
}
