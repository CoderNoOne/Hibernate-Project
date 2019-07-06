package service.entity;

import domain.Country;
import domain.Product;
import domain.Shop;
import exception.AppException;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.ShopRepositoryImpl;
import util.others.UserDataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static util.entity_utils.ShopUtil.getShopIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateShopUtil.getUpdatedShop;


public class ShopService {

  private final ShopRepository shopRepository;
  private final CountryService countryService;

  public ShopService() {
    shopRepository = new ShopRepositoryImpl();
    countryService = new CountryService();
  }

//  private Country getCountryFromDbIfExists(Country country) {
//    return countryService.getCountryByName(country.getName()).orElse(country);
//  }

  private Shop setShopComponentsFromDbIfTheyExist(Shop shop) {

    return Shop.builder()
            .id(shop.getId())
            .name(shop.getName())
            .country(countryService.getCountryFromDbIfExists(shop.getCountry()))
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

  public List<Shop> getAllShops() {
    return shopRepository.findAll();
  }

  public Optional<Shop> getShopById(Long shopId) {
    return shopRepository.findById(shopId);
  }

  public void updateShop() {
    printCollectionWithNumeration(getAllShops());

    long shopId = getInt("Choose shop id you want to update");

    getShopById(shopId)
            .ifPresentOrElse(shop ->
                    shopRepository.addOrUpdate(setShopComponentsFromDbIfTheyExist(getShopIfValid(getUpdatedShop(shop)))),
    () -> {
      throw new AppException("There is no shop with that id: " + shopId + " in DB");
    });

  }
}


