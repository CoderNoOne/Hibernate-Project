package service;

import domain.Product;
import domain.Shop;
import domain.Stock;
import exception.AppException;
import repository.abstract_repository.entity.StockRepository;
import repository.impl.StockRepositoryImpl;

import java.util.Optional;

public class StockService {

  private final StockRepository stockRepository;

  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
  }

  public Optional<Stock> addStockToDb(Stock stock) {
    return stockRepository.addOrUpdate(stock);
  }

  public void addStockToDbFromUserInput(Stock stock) {
    if (isStockUniqueByShopAndProduct(stock.getShop(), stock.getProduct())) {
      addStockToDb(stock);
    } else {
      throw new AppException("You couldn't add shop to db. Stock is not unique by .....");
    }
  }

  private boolean isStockUniqueByShopAndProduct(Shop shop, Product product) {
    return stockRepository.findStockByShopAndProduct(shop, product).isEmpty();
  }
}
