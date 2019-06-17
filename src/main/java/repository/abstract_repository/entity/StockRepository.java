package repository.abstract_repository.entity;

import domain.Product;
import domain.Shop;
import domain.Stock;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long> {
  Optional<Stock> findStockByShopAndProduct(Shop shop, Product product);

  Integer readStockQuantityById(Long id);
}
