package service.entity;

import domain.Shop;
import dto.CountryDto;
import dto.ShopDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.ShopRepositoryImpl;
import util.update.enums.ShopField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ShopUtil.getShopDtoIfValid;

@RequiredArgsConstructor
public class ShopService {

  private final ShopRepository shopRepository;

  private final CountryRepository countryRepository;

  public ShopService() {
    this.shopRepository = new ShopRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  private ShopDto setShopComponentsFromDbIfTheyExist(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("Shop object is null");
    }
    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryRepository.findCountryByName(shopDto.getCountryDto().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(shopDto.getCountryDto()))
            .build();
  }

  private Optional<ShopDto> addShopToDb(ShopDto shopDto) {

    Shop shop = ModelMapper.mapShopDtoToShop(shopDto);

    countryRepository
            .findCountryByName(shop.getCountry().getName())
            .ifPresentOrElse(shop::setCountry, () -> countryRepository.add(shop.getCountry()));

    shopRepository.add(shop);
    return Optional.of(ModelMapper.mapShopToShopDto(shop));

  }

  public void addShopToDbFromUserInput(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("ShopDto object is null");
    }

    if (isShopUniqueByNameAndCountry(shopDto.getName(), shopDto.getCountryDto())) {
      addShopToDb(/*setShopComponentsFromDbIfTheyExist*/(shopDto));
    } else {
      throw new AppException(String.format("You couldn't add shop to db. Shop is not unique by name: %s and country: %s", shopDto.getName(), shopDto.getCountryDto().getName()));
    }
  }

  private boolean isShopUniqueByNameAndCountry(String name, CountryDto countryDto) {
    if (name == null || name.equals("")) {
      throw new AppException(String.format("Shop name is undefined/null: %s", name));
    }

    return shopRepository
            .findShopByNameAndCountry(name, countryDto.getName()).isEmpty();
  }

  private Optional<ShopDto> getShopDtoByNameAndCountry(String name, String countryName) {

    if (name == null || countryName == null) {
      throw new AppException(String.format("Shop name or/and countryName is null: Shop name:%sCountry name: %s", name, countryName));
    }
    return shopRepository.findShopByNameAndCountry(name, countryName)
            .map(ModelMapper::mapShopToShopDto);
  }

  public List<ShopDto> getShopsByName(String name) {
    return shopRepository.findShopListByName(name)
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());
  }

  ShopDto getShopFromDbIfExists(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("ShopDto object is null");
    }

    return getShopDtoByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName()).orElse(shopDto);
  }

  public List<ShopDto> getAllShops() {
    return shopRepository.findAll()
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());
  }

  public Optional<ShopDto> getShopById(Long shopId) {

    if (shopId == null) {
      throw new AppException("ShopId is null");
    }

    return shopRepository.findById(shopId)
            .map(ModelMapper::mapShopToShopDto);
  }

  public void deleteShopDto(ShopDto shopDto) {
    if (shopDto == null) {
      throw new AppException("Shop is null/ undefined");
    }
    shopRepository.deleteShop(ModelMapper.mapShopDtoToShop(shopDto));
  }

  public void deleteAllShops() {
    shopRepository.deleteAll();
  }

  public Optional<ShopDto> update(ShopDto shopToUpdate, Map<ShopField, String> shopNewPropertyValues) {

    if (shopToUpdate == null || shopNewPropertyValues == null) {
      throw new AppException(String.format("Shop updateProduct method: inputs arguments are wrong (shopToUpdate: %s, shopNewPropertyValues: %s)", shopToUpdate, shopNewPropertyValues));
    }

    if (shopToUpdate.getId() == null) {
      throw new AppException("You cannot updateProduct a not persisted entity!");
    }
    shopNewPropertyValues.forEach((shopProperty, newValue) -> {

      switch (shopProperty) {
        case NAME -> shopToUpdate.setName(newValue);
        case COUNTRY -> shopToUpdate.setCountryDto(CountryDto.builder()
                .name(newValue)
                .build());
      }
    });

    return shopRepository.add(ModelMapper.mapShopDtoToShop(setShopComponentsFromDbIfTheyExist(getShopDtoIfValid(shopToUpdate))))
            .map(ModelMapper::mapShopToShopDto);

  }

  public ShopDto chooseShopToUpdate(Long shopId) {

    if (shopId == null) {
      throw new AppException("Wrong argument - shopId is null");
    }
    return getShopById(shopId)
            .orElseThrow(() -> new AppException("There is no shop with that id: " + shopId + " in DB"));
  }
}


