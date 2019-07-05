package validator.impl;

import domain.Shop;
import validator.AbstractValidator;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class ShopValidator extends AbstractValidator<Shop> {

  @Override
  public Map<String, String> validate(Shop shop) {
    errors.clear();
    if (shop == null) {
      errors.put("Shop object", "Shop object is null");
      return errors;
    }

    if (!isNameValid(shop)) {
      errors.put("Shop name", " Shop name should contain only capital letters and optionally white space between letters");
    }
    return errors;
  }

  private boolean isNameValid(Shop shop) {
    return shop.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
