package util.entity_utils;

import domain.Country;
import domain.Shop;
import domain.Stock;
import exception.AppException;
import validator.impl.ShopDtoValidator;


import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;


public final class ShopUtil {

  private ShopUtil() {
  }

  public static Shop createShopFromUserInput() {

    return Shop.builder()
            .name(getString("Input shop name"))
            .country(Country.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  public static Shop preciseShopDetails(Stock stock) {

    printMessage(String.format("Any shop with specified name:%s exists in a DB. You need to specify more shop details: "
            , stock.getShop().getName()));

    return Shop.builder()
            .name(stock.getShop().getName())
            .country(Country.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  public static Shop getShopIfValid(Shop shop) {

    var shopValidator = new ShopDtoValidator();
    var errorsMap = shopValidator.validate(shop);

    if (shopValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Shop is not valid");
    }
    return shop;
  }

  public static Shop chooseAvailableShop(List<Shop> shopList) {

    if(shopList.isEmpty()){
      throw new AppException("There are no shops which meet specified criteria");
    }
    int shopNumber;
    do {
      printCollectionWithNumeration(shopList.stream().map(Shop::getName).collect(Collectors.toList()));
      shopNumber = getInt("Choose shop by number");
    } while (!(shopNumber >= 1 && shopNumber <= shopList.size()));

    return shopList.get(shopNumber - 1);
  }

}
