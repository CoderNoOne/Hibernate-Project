package validator.impl;

import domain.Shop;
import validator.Validator;

import java.util.Map;

public class ShopValidator implements Validator <Shop> {

  @Override
  public Map<String, String> validate(Shop shop) {
    return null;
  }

  @Override
  public boolean hasErrors() {
    return false;
  }

  @Override
  public boolean validateEntity(Shop shop) {
    return false;
  }
}
