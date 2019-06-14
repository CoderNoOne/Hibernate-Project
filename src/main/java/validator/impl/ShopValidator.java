package validator.impl;

import domain.Shop;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class ShopValidator implements Validator<Shop> {


  private Map<String, String> errors;

  public ShopValidator() {
    errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Shop shop) {

    if (shop == null) {
      errors.put("Shop object", "Shop object is null");
      return errors;
    }

    if (!isNameValid(shop)) {
      errors.put("Shop name"," Shop name should contain only capital letters and optionally white space between letters");
    }
    return errors;
  }

  private boolean isNameValid(Shop shop) {
    return shop.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }


}
