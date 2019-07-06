package service.entity;

import domain.Product;
import domain.Shop;
import domain.Stock;
import dto.ProducerDto;
import exception.AppException;
import mapper.ProducerMapper;
import mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import repository.abstract_repository.entity.StockRepository;
import repository.impl.StockRepositoryImpl;
import util.entity_utils.StockUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.StockUtil.getStockIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;

public class StockService {

  private final StockRepository stockRepository;
  private final ProducerMapper producerMapper;

  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.producerMapper = Mappers.getMapper(ProducerMapper.class);
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

  public Map<Shop, Integer> getShopListWithProductInStock(Product product) {
    return stockRepository.findShopsWithProductInStock(product);
  }

  public void decreaseStockQuantityBySpecifiedAmount(Stock stock, Integer quantity) {
    stock.setQuantity(getStockQuantity(stock) - quantity);
    addStockToDb(stock);
  }

  public List<ProducerDto> getProcucersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts){
    return stockRepository
            .findProducersWithTradeAndNumberOfProducedProductsGreaterThan(tradeName, minAmountOfProducts).stream()
            .map(producerMapper::producerToProducerDto).collect(Collectors.toList());
  }

  public void updateStock() {
    printCollectionWithNumeration(getAllStocks());

    long stockId = getInt("Choose stock id you want to update");

    getStockById(stockId)
            .ifPresentOrElse(stock->
                            stockRepository.addOrUpdate(setStockComponentsFromDbIfTheyExist(getStockIfValid(getUpdatedStock(stock)))),
                    () -> {
                      throw new AppException("There is no stock with that id: " + stockId + " in DB");
                    });

  }

  private Optional<Stock> getStockById(long stockId) {
    return stockRepository.findById(stockId);
  }

  public List<Stock> getAllStocks() {
    return stockRepository.findAll();
  }
}
