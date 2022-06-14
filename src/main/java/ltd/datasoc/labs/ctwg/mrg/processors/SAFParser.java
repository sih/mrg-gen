package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.UNABLE_TO_PARSE_SAF;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;

/**
 * @author sih
 */
final class SAFParser {

  private final ObjectMapper yamlMapper;

  public SAFParser() {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.findAndRegisterModules();
  }

  public SAFModel parse(String safAsString) throws MRGGenerationException {
    try {
      return yamlMapper.readValue(safAsString, SAFModel.class);
    } catch (Exception e) {
      throw new MRGGenerationException(UNABLE_TO_PARSE_SAF);
    }
  }
}
