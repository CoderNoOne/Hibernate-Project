package utils.entity_utils;

import domain.Country;
import domain.Shop;
import exception.AppException;
import validator.impl.ShopValidator;


import java.util.stream.Collectors;

import static utils.UserDataUtils.getString;
import static utils.UserDataUtils.printMessage;


public final class ShopUtil {

  private static final ShopValidator shopValidator = new ShopValidator();

  private ShopUtil() {
  }

  public static Shop createShopFromUserInput() {

    var shopName = getString("Input shop name");

    var country = Country.builder().name(getString("Input country name")).build();

    Shop shop = Shop.builder().name(shopName).country(country).build();
    var errorsMap = shopValidator.validate(shop);

    if (shopValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Shop is not valid");
    }

    return shop;

  }
}
