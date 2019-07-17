package validator.impl;

import dto.ShopDto;
import validator.AbstractValidator;
import java.util.Map;

public class ShopDtoValidator extends AbstractValidator<ShopDto> {

  @Override
  public Map<String, String> validate(ShopDto shopDto) {
    errors.clear();
    if (shopDto == null) {
      errors.put("Shop object", "Shop object is null");
      return errors;
    }

    if (!isNameValid(shopDto)) {
      errors.put("Shop name", " Shop name should contain only capital letters and optionally white space between letters");
    }
    return errors;
  }

  private boolean isNameValid(ShopDto shopDto) {
    return shopDto.getName() != null && shopDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
