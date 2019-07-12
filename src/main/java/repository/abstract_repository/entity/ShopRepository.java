package repository.abstract_repository.entity;

import domain.Shop;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends CrudRepository<Shop, Long> {

  Optional<Shop> findShopByNameAndCountry(String name, String countryName);

  List<Shop> findShopListByName(String name);
}
