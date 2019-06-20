package repository.abstract_repository.entity;

import domain.*;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long> {
  Optional<Stock> findStockByShopAndProduct(Shop shop, Product product);

//  Integer readStockQuantityById(Long id);

  Map<Shop, Integer> findShopsWithProductInStock(Product product);

  List<Producer> findProducersWithTradeAndNumberOfProducedProductsGreaterThan(String tradeName, Integer minAmountOfProducts);
}
