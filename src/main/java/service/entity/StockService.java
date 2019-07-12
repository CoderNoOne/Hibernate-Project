package service.entity;

import domain.CustomerOrderDto;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.*;
import static util.entity_utils.ShopUtil.*;
import static util.entity_utils.StockUtil.getStockIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateStockUtil.getUpdatedStock;

public class StockService {

  private final StockRepository stockRepository;
  private final ShopService shopService;
  private final ProductService productService;
  private final CountryService countryService;
  private final ProducerMapper producerMapper;

  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.producerMapper = Mappers.getMapper(ProducerMapper.class);
    this.shopService = new ShopService();
    this.productService = new ProductService();
    this.countryService = new CountryService();
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

  private Stock setStockComponentsFromDbIfTheyExist(Stock stock) {
    return Stock.builder()
            .id(stock.getId())
            .quantity(stock.getQuantity())
            .shop(shopService.getShopFromDbIfExists(stock.getShop()))
            .product(productService.getProductFromDbIfExists(stock.getProduct()))
            .build();
  }


  public Stock specifyShop(Stock stock) {

    var shopsByName = shopService.getShopsByName(stock.getShop().getName());

    var shop = !shopsByName.isEmpty() ?
            chooseAvailableShop(shopsByName) : setShopComponentsFromDbIfTheyExist(getShopIfValid(preciseShopDetails(stock)));

    stock.setShop(shop);
    return stock;

  }

  private Shop setShopComponentsFromDbIfTheyExist(Shop shop) {

    return Shop.builder()
            .name(shop.getName())
            .country(countryService.getCountryFromDbIfExists(shop.getCountry()))
            .build();
  }

  private boolean isStockUniqueByShopAndProduct(Shop shop, Product product) {
    return stockRepository.findStockByShopAndProduct(shop, product).isEmpty();
  }

  public void decreaseStockQuantityIfValid(Map<Shop, Integer> map, CustomerOrderDto customerOrder) {

    getStockByShopAndProduct(map.keySet().iterator().next(), customerOrder.getProduct())
            .ifPresentOrElse(stock -> {
              if (stock.getQuantity() >= customerOrder.getQuantity()) {
                decreaseStockQuantityBySpecifiedAmount(stock, customerOrder.getQuantity());
              } else {
                throw new AppException(String.format("Not enough products in stock. Customer wants %d but there are only %d products in the stock",
                        customerOrder.getQuantity(), stock.getQuantity()));
              }
            }, () -> {
              throw new AppException(String.format("No stock was found for product: %s and for shop: %s",
                      customerOrder.getProduct().getName(), map.keySet().iterator().next().getName()));
            });
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

  public List<ProducerDto> getProducersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts) {
    return stockRepository
            .findProducersWithTradeAndNumberOfProducedProductsGreaterThan(tradeName, minAmountOfProducts).stream()
            .map(producerMapper::producerToProducerDto).collect(Collectors.toList());
  }

  public void updateStock() {
    printCollectionWithNumeration(getAllStocks());

    long stockId = getInt("Choose stock id you want to update");

    getStockById(stockId)
            .ifPresentOrElse(stock ->
                            stockRepository.addOrUpdate(setStockComponentsFromDbIfTheyExist(getStockIfValid(getUpdatedStock(stock)))),
                    () -> {
                      throw new AppException("There is no stock with that id: " + stockId + " in DB");
                    });

  }

  public Stock specifyProduct(Stock stock) {

    var productList = productService.getProductsByNameAndCategory(stock.getProduct().getName(),
            stock.getProduct().getCategory());

    var product = !productList.isEmpty() ? chooseAvailableProduct(productList) : productService.setProductComponentsFromDbIfTheyExist(
            getProductIfValid(preciseProductDetails(stock)));

    stock.setProduct(product);

    return stock;
  }


  private Optional<Stock> getStockById(long stockId) {
    return stockRepository.findById(stockId);
  }

  public List<Stock> getAllStocks() {
    return stockRepository.findAll();
  }

  public void deleteAllStocks() {
    stockRepository.deleteAll();
  }
}
