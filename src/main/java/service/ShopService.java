package service;

import domain.Country;
import domain.Shop;
import exception.AppException;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.ShopRepositoryImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class ShopService {

  private final ShopRepository shopRepository;


  public ShopService() {
    shopRepository = new ShopRepositoryImpl();
  }


  public Optional<Shop> addShopToDb(Shop shop) {

    return shopRepository.addOrUpdate(shop);
  }


  public void addShopToDbFromUserInput(Shop shop) {
    if (isShopUniqueByNameAndCountry(shop.getName(), shop.getCountry())) {
      addShopToDb(shop);
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
           /* || !shopRepository.findShopByNameAndCountry(name, country.getName()).get()
            .getCountry().getName().equals(country.getName());*/

  }

  public Optional<Shop> getShopByNameAndCountry(String name, String countryName) {
    return shopRepository.findShopByNameAndCountry(name, countryName);

  }

  public List<Shop> getShopsByName(String name) {
    return shopRepository.findShopListByName(name);
  }
}


