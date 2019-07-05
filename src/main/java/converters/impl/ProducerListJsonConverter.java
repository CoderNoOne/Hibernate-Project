package converters.impl;

import converters.JsonConverter;
import domain.Producer;

import java.util.List;

public class ProducerListJsonConverter extends JsonConverter <List<Producer>> {

  public ProducerListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
