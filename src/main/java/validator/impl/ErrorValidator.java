package validator.impl;

import validator.Validator;
import domain.Error;

import java.util.Map;

public class ErrorValidator implements Validator<Error> {
  @Override
  public Map<String, String> validate(Error error) {
    return null;
  }

  @Override
  public boolean hasErrors() {
    return false;
  }

  @Override
  public boolean validateEntity(Error error) {
    return false;
  }
}
