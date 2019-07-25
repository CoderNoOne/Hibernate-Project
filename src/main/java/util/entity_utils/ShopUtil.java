package util.entity_utils;

import dto.CountryDto;
import dto.ShopDto;
import dto.StockDto;
import exception.AppException;
import util.update.enums.ShopField;
import validator.impl.ShopDtoValidator;


import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public interface ShopUtil {


  static Map<ShopField, String> getUpdatedFields() {

    List<ShopField> shopFields = Arrays.stream(ShopField.values()).collect(Collectors.toList());
    Map<ShopField, String> fieldsToUpdate = new EnumMap<>(ShopField.class);

    int shopFieldNumber;

    do {

      do {
        printCollectionWithNumeration(shopFields);
        shopFieldNumber = getInt("Choose what shop property you want to updateProduct");
      } while (!(shopFieldNumber >= 1 && shopFieldNumber <= shopFields.size()));

      switch (shopFields.get(shopFieldNumber - 1)) {

        case NAME -> {
          fieldsToUpdate.put(ShopField.NAME, getString("Type shop new name"));
          shopFields.remove(ShopField.NAME);
        }
        case COUNTRY -> {
          shopFields.remove(ShopField.COUNTRY);
          fieldsToUpdate.put(ShopField.COUNTRY, getString("Type shop new country"));
        }
      }

    } while (!shopFields.isEmpty() && getString("Do you want to updateProduct other shop property? (Y/N)").equalsIgnoreCase("Y"));

    return fieldsToUpdate;
  }

  static ShopDto createShopDtoFromUserInput() {

    return ShopDto.builder()
            .name(getString("Input shop name"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  static ShopDto preciseShopDtoDetails(StockDto stockDto) {

    printMessage(String.format("Any shop with specified name:%s exists in a DB. You need to specify more shop details: "
            , stockDto.getShopDto().getName()));

    return ShopDto.builder()
            .name(stockDto.getShopDto().getName())
            .countryDto(CountryDto.builder()
                    .name(getString("Input shop country"))
                    .build())
            .build();
  }

  static ShopDto getShopDtoIfValid(ShopDto shopDto) {

    var shopValidator = new ShopDtoValidator();
    var errorsMap = shopValidator.validate(shopDto);

    if (shopValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Shop is not valid");
    }
    return shopDto;
  }

  static ShopDto chooseAvailableShop(List<ShopDto> shopList) {

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

  static ShopDto specifyShopDtoDetailToDelete() {

    printMessage("\nInput country's information you want to delete\n");
    return createShopDtoFromUserInput();
  }

}
