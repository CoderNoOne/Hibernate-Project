package repository.abstract_repository.entity;

import domain.Shop;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface ShopRepository extends CrudRepository<Shop, Long> {

  Optional<Shop> findShopByNameAndCountry(String name, Long countryId);
}
