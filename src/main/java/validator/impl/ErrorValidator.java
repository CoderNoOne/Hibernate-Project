package validator.impl;

import validator.AbstractValidator;
import domain.Error;

import java.util.Map;


public class ErrorValidator extends AbstractValidator <Error> {

  @Override
  public Map<String, String> validate(Error error) {
    return errors;
  }

}
