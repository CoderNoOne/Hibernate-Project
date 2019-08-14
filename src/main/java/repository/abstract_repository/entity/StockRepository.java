package repository.abstract_repository.entity;

import domain.*;
import dto.ProductDto;
import dto.ShopDto;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long> {

  Optional<Stock> findStockByShopAndProduct(Shop shop, Product product);

  Map<Shop, Integer> findShopsWithProductInStock(Product product);

  List<Stock> findStocksWithProducerTradeName(String tradeName) ;

  void decreaseStockQuantity(Stock stock, Integer quantity);

  void increaseStockQuantity(Long stockId, Integer quantity);

}
