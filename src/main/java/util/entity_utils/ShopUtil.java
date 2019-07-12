package util.entity_utils;

import dto.CountryDto;
import dto.ShopDto;
import dto.StockDto;
import exception.AppException;
import validator.impl.ShopDtoValidator;


import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;


public final class ShopUtil {

  private ShopUtil() {
  }

  public static ShopDto createShopDtoFromUserInput() {

    return ShopDto.builder()
            .name(getString("Input shop name"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  public static ShopDto preciseShopDtoDetails(StockDto stockDto) {

    printMessage(String.format("Any shop with specified name:%s exists in a DB. You need to specify more shop details: "
            , stockDto.getShopDto().getName()));

    return ShopDto.builder()
            .name(stockDto.getShopDto().getName())
            .countryDto(CountryDto.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  public static ShopDto getShopDtoIfValid(ShopDto shopDto) {

    var shopValidator = new ShopDtoValidator();
    var errorsMap = shopValidator.validate(shopDto);

    if (shopValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Shop is not valid");
    }
    return shopDto;
  }

  public static ShopDto chooseAvailableShop(List<ShopDto> shopList) {

    if (shopList.isEmpty()) {
      throw new AppException("There are no shops which meet specified criteria");
    }
    int shopNumber;
    do {
      printCollectionWithNumeration(shopList.stream().map(ShopDto::getName).collect(Collectors.toList()));
      shopNumber = getInt("Choose shop by number");
    } while (!(shopNumber >= 1 && shopNumber <= shopList.size()));

    return shopList.get(shopNumber - 1);
  }

}
