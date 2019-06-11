package utils.entity_utils;

import domain.Country;
import domain.Shop;
import service.ShopService;
import validator.impl.ShopValidator;



import static utils.UserDataUtils.getString;


public final class ShopUtil {

  private static final ShopValidator shopValidator = new ShopValidator();
  private static final ShopService shopService = new ShopService();

  private ShopUtil() {
  }

  public static Shop createShopFromUserInput() {

    var shopName = getString("Input shop name");

    var country = Country.builder().name(getString("Input country name")).build();

    return Shop.builder().name(shopName).country(country).build();

  }
}
