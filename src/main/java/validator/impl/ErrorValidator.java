package validator.impl;

import validator.Validator;
import domain.Error;

import java.util.Map;

/*czy tu będzie validator?*/
public class ErrorValidator implements Validator<Error> {
  @Override
  public Map<String, String> validate(Error error) {
    return null;
  }

  @Override
  public boolean hasErrors() {
    return false;
  }

}
