package validator.impl;

import domain.GuaranteeComponent;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class GuaranteeComponentsValidator implements Validator<GuaranteeComponent> {

  private Map<String, String> errors;

  public GuaranteeComponentsValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(GuaranteeComponent guaranteeComponent) {

    if (guaranteeComponent == null) {
      errors.put("GuaranteeComponent object", "Guarantee component object is null");
    }
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }
}
