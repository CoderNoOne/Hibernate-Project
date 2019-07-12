package mappers;

import domain.Country;
import domain.Shop;
import dto.CountryDto;
import dto.ShopDto;

public class ShopMapper {

  public ShopDto mapShopToShopDto(Shop shop) {
    return ShopDto.builder()
            .id(shop.getId())
            .name(shop.getName())
            .countryDto(CountryDto.builder()
                    .id(shop.getCountry().getId())
                    .name(shop.getCountry().getName())
                    .build())
            .build();
  }


  public Shop mapShopDtoToShop(ShopDto shopDto) {

    return Shop.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .country(Country.builder()
                    .id(shopDto.getCountryDto().getId())
                    .name(shopDto.getCountryDto().getName())
                    .build())
            .build();
  }
}
