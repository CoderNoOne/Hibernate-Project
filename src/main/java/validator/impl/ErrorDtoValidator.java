package validator.impl;

import dto.ErrorDto;
import validator.AbstractValidator;

import java.util.Map;


public class ErrorDtoValidator extends AbstractValidator <ErrorDto> {

  @Override
  public Map<String, String> validate(ErrorDto error) {
    return errors;
  }

}
