package validator.impl;

import domain.Producer;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class ProducerValidator implements Validator<Producer> {

  private Map<String, String> errors;

  public ProducerValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Producer producer) {

    if (producer == null) {
      errors.put("Producer object", "Producer object is null");
      return errors;
    }

    if(!isProducerNameValid(producer)){
      errors.put("Producer name", "Producer name should contain only capital letters and optionally a whitespace between them");
    }
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isProducerNameValid(Producer producer) {
    return producer.getName().matches("[A-Z]+(\\s[A-Z])*");
  }
}
