package util.update;

import dto.CountryDto;
import dto.ShopDto;
import util.update.enums.ShopField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getString;

public interface UpdateShopUtil {

  static ShopDto getUpdatedShop(ShopDto shopDto){

    List<ShopField> shopFields = Arrays.stream(ShopField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(shopFields);
      ShopField shopProperty = ShopField.valueOf(getString("Choose what shop property you want to update. Not case sensitive").toUpperCase());

      switch (shopProperty) {
        case NAME -> {
          String updatedName = getString("Type shop new name");
          shopFields.remove(ShopField.NAME);
          shopDto.setName(updatedName);
        }
        case COUNTRY -> {
          String updatedCountryName = getString("Type shop new country");
          shopFields.remove(ShopField.COUNTRY);
          shopDto.setCountryDto(CountryDto.builder().name(updatedCountryName).build());
        }
        default -> System.out.println("Not valid shop property");
      }
      hasNext = getString("Do you want to update other shop property? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !shopFields.isEmpty());

    return shopDto;
  }
}
