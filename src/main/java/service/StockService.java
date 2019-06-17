package service;

import domain.CustomerOrder;
import domain.Product;
import domain.Shop;
import domain.Stock;
import exception.AppException;
import repository.abstract_repository.entity.StockRepository;
import repository.impl.StockRepositoryImpl;

import java.util.List;
import java.util.Map;
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
    if (!isStockUniqueByShopAndProduct(stock.getShop(), stock.getProduct())) {
      var stockFromDb = getStockByShopAndProduct(stock.getShop(), stock.getProduct()).orElseThrow(() -> new AppException("Stock doesn't exist in db"));
      stockFromDb.setQuantity(getStockQuantity(stockFromDb) + stock.getQuantity());
      stock = stockFromDb;
    }
    addStockToDb(stock);
  }

  private boolean isStockUniqueByShopAndProduct(Shop shop, Product product) {
    return stockRepository.findStockByShopAndProduct(shop, product).isEmpty();
  }

  private Integer getStockQuantity(Stock stock) {
    return stockRepository.findStockByShopAndProduct(stock.getShop(), stock.getProduct())
            .orElseThrow(() -> new AppException("No such stock exists")).getQuantity();
  }

  public Optional<Stock> getStockByShopAndProduct(Shop shop, Product product) {
    return stockRepository.findStockByShopAndProduct(shop, product);
  }

  public Map<Shop,Integer> getShopListWithProductInStock(Product product) {
    return stockRepository.findShopsWithProductInStock(product);
  }
}
