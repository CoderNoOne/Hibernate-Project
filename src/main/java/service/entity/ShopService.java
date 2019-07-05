package service.entity;

import domain.Country;
import domain.Product;
import domain.Shop;
import exception.AppException;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.ShopRepositoryImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class ShopService {

  private final ShopRepository shopRepository;
  private final CountryService countryService;

  public ShopService() {
    shopRepository = new ShopRepositoryImpl();
    countryService = new CountryService();
  }

  private Country getCountryFromDbIfExists(Country country) {
    return countryService.getCountryByName(country.getName()).orElse(country);
  }

  private Shop setShopComponentsFromDbIfTheyExist(Shop shop) {

    return Shop.builder()
            .name(shop.getName())
            .country(getCountryFromDbIfExists(shop.getCountry()))
            .build();
  }

  public Optional<Shop> addShopToDb(Shop shop) {

    return shopRepository.addOrUpdate(shop);
  }

  public void addShopToDbFromUserInput(Shop shop) {
    if (isShopUniqueByNameAndCountry(shop.getName(), shop.getCountry())) {
      addShopToDb(setShopComponentsFromDbIfTheyExist(shop));
    } else {
      throw new AppException("You couldn't add shop to db. Shop is not unique by name and country");
    }
  }

  private boolean isShopUniqueByNameAndCountry(String name, Country country) {
    if (name == null) {
      throw new AppException("Shop name is null");
    }

    return shopRepository
            .findShopByNameAndCountry(name, country.getName()).isEmpty();
  }

  public Optional<Shop> getShopByNameAndCountry(String name, String countryName) {
    return shopRepository.findShopByNameAndCountry(name, countryName);

  }

  public List<Shop> getShopsByName(String name) {
    return shopRepository.findShopListByName(name);
  }

  public void deleteAllShops() {
    shopRepository.deleteAll();
  }
}


