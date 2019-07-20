package service.entity;

import dto.*;
import exception.AppException;
import mapper.*;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ProductRepository;
import repository.abstract_repository.entity.ShopRepository;
import repository.abstract_repository.entity.StockRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.ProductRepositoryImpl;
import repository.impl.ShopRepositoryImpl;
import repository.impl.StockRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.*;
import static util.entity_utils.ShopUtil.*;
import static util.entity_utils.StockUtil.getStockDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateStockUtil.getUpdatedStock;

public class StockService {

  private final StockRepository stockRepository;

  private final CountryRepository countryRepository;
  private final ProductRepository productRepository;
  private final ShopRepository shopRepository;


  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.shopRepository = new ShopRepositoryImpl();
    this.productRepository = new ProductRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public StockService(StockRepository stockRepository, ShopRepository shopRepository, ProductRepository productRepository, CountryRepository countryRepository) {
    this.stockRepository = stockRepository;
    this.shopRepository = shopRepository;
    this.productRepository = productRepository;
    this.countryRepository = countryRepository;
  }


  private Optional<StockDto> addStockToDb(StockDto stockDto) {
    return stockRepository.addOrUpdate(ModelMapper.mapStockDtoToStock(stockDto))
            .map(ModelMapper::mapStockToStockDto);
  }

  public void addStockToDbFromUserInput(StockDto stockDto) {

    if (stockDto == null) {
      throw new AppException("StockDto object is null");
    }

    if (!isStockUniqueByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto())) {
      var stockFromDb = getStockByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto()).orElseThrow(() -> new AppException("Stock doesn't exist in db"));
      stockFromDb.setQuantity(getStockQuantity(stockFromDb) + stockDto.getQuantity());
      stockDto = stockFromDb;
    }

    addStockToDb(setStockDtoComponentsFromDbIfTheyExist(stockDto));
  }

  private StockDto setStockDtoComponentsFromDbIfTheyExist(StockDto stockDto) {

    return StockDto.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .shopDto(shopRepository.findShopByNameAndCountry(
                    stockDto.getShopDto().getName(),
                    stockDto.getShopDto().getCountryDto().getName())
                    .map(ModelMapper::mapShopToShopDto)
                    .orElse(stockDto.getShopDto()))
            .productDto(productRepository.findByNameAndCategoryAndProducer(
                    stockDto.getProductDto().getName(),
                    ModelMapper.mapCategoryDtoToCategory(stockDto.getProductDto().getCategoryDto()),
                    ModelMapper.mapProducerDtoToProducer(stockDto.getProductDto().getProducerDto()))
                    .map(ModelMapper::mapProductToProductDto)
                    .orElse(stockDto.getProductDto()))
            .build();
  }


  public StockDto specifyShop(StockDto stockDto) {

    if (stockDto == null) {
      throw new AppException("StockDto object is null");
    }

    if (stockDto.getShopDto() == null || stockDto.getShopDto().getName() == null) {
      throw new AppException("Shop is null/undefinied: " + stockDto.getShopDto());
    }

    var shopsByName = shopRepository.findShopListByName(stockDto.getShopDto().getName())
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());


    var shopDto = !shopsByName.isEmpty() ?
            chooseAvailableShop(shopsByName) : setShopComponentsFromDbIfTheyExist(getShopDtoIfValid(preciseShopDtoDetails(stockDto)));

    stockDto.setShopDto(shopDto);
    return stockDto;

  }

  private ShopDto setShopComponentsFromDbIfTheyExist(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("ShopDto is null");
    }

    if (shopDto.getCountryDto() == null) {
      throw new AppException("Shop country is null");
    }

    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryRepository.findCountryByName(shopDto.getCountryDto().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(shopDto.getCountryDto()))
            .build();
  }

  private boolean isStockUniqueByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository.findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto)).isEmpty();
  }

  public void decreaseStockQuantityIfValid(Map<ShopDto, Integer> map, CustomerOrderDto customerOrder) {

    getStockByShopAndProduct(map.keySet().iterator().next(), customerOrder.getProduct())
            .ifPresentOrElse(stockDto -> {
              if (stockDto.getQuantity() >= customerOrder.getQuantity()) {
                decreaseStockQuantityBySpecifiedAmount(stockDto, customerOrder.getQuantity());
              } else {
                throw new AppException(String.format("Not enough products in stock. Customer wants %d but there are only %d products in the stock",
                        customerOrder.getQuantity(), stockDto.getQuantity()));
              }
            }, () -> {
              throw new AppException(String.format("No stock was found for product: %s and for shop: %s",
                      customerOrder.getProduct().getName(), map.keySet().iterator().next().getName()));
            });
  }

  private Integer getStockQuantity(StockDto stockDto) {
    return stockRepository.findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(stockDto.getShopDto()), ModelMapper.mapProductDtoToProduct(stockDto.getProductDto()))
            .orElseThrow(() -> new AppException("No such stock exists")).getQuantity();
  }

  private Optional<StockDto> getStockByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository
            .findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto))
            .map(ModelMapper::mapStockToStockDto);
  }

  Map<ShopDto, Integer> getShopListWithProductInStock(ProductDto productDto) {
    return stockRepository.findShopsWithProductInStock(ModelMapper.mapProductDtoToProduct(productDto))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                    e -> ModelMapper.mapShopToShopDto(e.getKey()),
                    Map.Entry::getValue
            ));
  }

  private void decreaseStockQuantityBySpecifiedAmount(StockDto stockDto, Integer quantity) {
    stockDto.setQuantity(getStockQuantity(stockDto) - quantity);
    addStockToDb(stockDto);
  }

  public List<ProducerDto> getProducersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts) {
    return stockRepository
            .findProducersWithTradeAndNumberOfProducedProductsGreaterThan(tradeName, minAmountOfProducts).stream()
            .map(ModelMapper::mapProducerToProducerDto).collect(Collectors.toList());
  }

  public void updateStock() {
    printCollectionWithNumeration(getAllStocks());

    long stockId = getInt("Choose stock id you want to update");

    getStockById(stockId)
            .ifPresentOrElse(stockDto ->
                            stockRepository
                                    .addOrUpdate(ModelMapper
                                            .mapStockDtoToStock(setStockDtoComponentsFromDbIfTheyExist(getStockDtoIfValid(getUpdatedStock(stockDto))))),
                    () -> {
                      throw new AppException("There is no stock with that id: " + stockId + " in DB");
                    });

  }

  public StockDto specifyProduct(StockDto stockDto) {

    var productList = productRepository.findProductsByNameAndCategory(stockDto.getProductDto().getName(), ModelMapper.mapCategoryDtoToCategory(stockDto.getProductDto().getCategoryDto()))
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());


    var productDto = !productList.isEmpty() ? chooseAvailableProduct(productList) : getProductIfValid(preciseProductDtoDetails(stockDto));

    stockDto.setProductDto(productDto);

    return stockDto;
  }


  private Optional<StockDto> getStockById(long stockId) {
    return stockRepository.findShopById(stockId)
            .map(ModelMapper::mapStockToStockDto);
  }

  private List<StockDto> getAllStocks() {

    return stockRepository.findAll()
            .stream()
            .map(ModelMapper::mapStockToStockDto)
            .collect(Collectors.toList());
  }

  public void deleteAllStocks() {
    stockRepository.deleteAll();
  }
}
