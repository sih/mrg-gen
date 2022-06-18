package ltd.datasoc.labs.ctwg.mrg.processors;

import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.CANNOT_PARSE_TERM;
import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.CANNOT_WRITE_MRG;
import static ltd.datasoc.labs.ctwg.mrg.processors.MRGGenerationException.UNABLE_TO_PARSE_SAF;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import ltd.datasoc.labs.ctwg.mrg.model.MRGModel;
import ltd.datasoc.labs.ctwg.mrg.model.SAFModel;
import ltd.datasoc.labs.ctwg.mrg.model.Term;

/**
 * @author sih
 */
final class YamlWrangler {

  private final ObjectMapper yamlMapper;

  YamlWrangler() {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.findAndRegisterModules();
  }

  SAFModel parseSaf(String safAsString) throws MRGGenerationException {
    try {
      return yamlMapper.readValue(safAsString, SAFModel.class);
    } catch (Exception e) {
      throw new MRGGenerationException(UNABLE_TO_PARSE_SAF);
    }
  }

  Term parseTerm(String termString) throws MRGGenerationException {
    try {
      return yamlMapper.readValue(termString, Term.class);
    } catch (Exception e) {
      throw new MRGGenerationException(String.format(CANNOT_PARSE_TERM, termString));
    }
  }

  void writeMrg(Path location, MRGModel mrg) throws MRGGenerationException {
    try (OutputStream fos = Files.newOutputStream(location)) {
      yamlMapper.writeValue(fos, mrg);
    } catch (IOException ioException) {
      throw new MRGGenerationException(
          String.format(CANNOT_WRITE_MRG, location.toAbsolutePath().toString()));
    }
  }
}
