package service.entity;

import domain.Stock;
import dto.*;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.*;
import static util.entity_utils.ShopUtil.*;
import static util.entity_utils.StockUtil.getStockDtoIfValid;

public class StockService {

  private final StockRepository stockRepository;

  private final CountryRepository countryRepository;
  private final ProductRepository productRepository;
  private final ShopRepository shopRepository;
  private final CategoryRepository categoryRepository;
  private final ProducerRepository producerRepository;
  private final TradeRepository tradeRepository;


  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.shopRepository = new ShopRepositoryImpl();
    this.productRepository = new ProductRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
    this.categoryRepository = new CategoryRepositoryImpl();
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeRepository = new TradeRepositoryImpl();
  }

  public StockService(StockRepository stockRepository, ShopRepository shopRepository, ProductRepository productRepository, CountryRepository countryRepository, CategoryRepository categoryRepository, ProducerRepository producerRepository, TradeRepository tradeRepository) {
    this.stockRepository = stockRepository;
    this.shopRepository = shopRepository;
    this.productRepository = productRepository;
    this.countryRepository = countryRepository;
    this.categoryRepository = categoryRepository;
    this.producerRepository = producerRepository;
    this.tradeRepository = tradeRepository;
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
            .shopDto(setShopComponentFromDb(shopRepository.findShopByNameAndCountry(
                    stockDto.getShopDto().getName(),
                    stockDto.getShopDto().getCountryDto().getName())
                    .map(ModelMapper::mapShopToShopDto)
                    .orElse(stockDto.getShopDto())))
            .productDto(setProductComponentFromDb(productRepository.findByNameAndCategoryAndProducer(
                    stockDto.getProductDto().getName(),
                    ModelMapper.mapCategoryDtoToCategory(stockDto.getProductDto().getCategoryDto()),
                    ModelMapper.mapProducerDtoToProducer(stockDto.getProductDto().getProducerDto()))
                    .map(ModelMapper::mapProductToProductDto)
                    .orElse(stockDto.getProductDto())))
            .build();
  }

  private ProductDto setProductComponentFromDb(ProductDto productDto) {

    return ProductDto.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .price(productDto.getPrice())
            .producerDto(setProducerComponent(producerRepository.findByNameAndTradeAndCountry(productDto.getProducerDto().getName(),
                    ModelMapper.mapTradeDtoToTrade(productDto.getProducerDto().getTrade()),
                    ModelMapper.mapCountryDtoToCountry(productDto.getProducerDto().getCountry()))
                    .map(ModelMapper::mapProducerToProducerDto)
                    .orElse(productDto.getProducerDto())))
            .categoryDto(categoryRepository.findCategoryByName(productDto.getCategoryDto().getName())
                    .map(ModelMapper::mapCategoryToCategoryDto).orElse(productDto.getCategoryDto()))
            .build();
  }

  private ProducerDto setProducerComponent(ProducerDto producerDto) {
    return ProducerDto.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .country(countryRepository.findCountryByName(producerDto.getCountry().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(producerDto.getCountry()))
            .trade(tradeRepository.findTradeByName(producerDto.getTrade().getName())
                    .map(ModelMapper::mapTradeToTradeDto)
                    .orElse(producerDto.getTrade()))
            .build();
  }

  private ShopDto setShopComponentFromDb(ShopDto shopDto) {
    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryRepository.findCountryByName(shopDto.getCountryDto().getName())
                    .map(ModelMapper::mapCountryToCountryDto).orElse(shopDto.getCountryDto()))
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

  public Optional<StockDto> updateStock(StockDto stockDtoToUpdate) {

    Long id = stockDtoToUpdate.getId();

    if (id == null) {
      throw new AppException("Stock id is null");
    }

    Stock stockFromDb = stockRepository.findById(id)
            .orElseThrow(() -> new AppException("Stock with id: " + id + " doesn't exist in DB yet"));

    StockDto stockToUpdate = StockDto.builder()
            .id(id)
            .quantity(stockDtoToUpdate.getQuantity() != null ? stockDtoToUpdate.getQuantity() : stockFromDb.getQuantity())
            .productDto(ModelMapper.mapProductToProductDto(stockFromDb.getProduct()))
            .shopDto(ModelMapper.mapShopToShopDto(stockFromDb.getShop()))
            .build();

    return stockRepository
            .addOrUpdate(ModelMapper.mapStockDtoToStock(getStockDtoIfValid(setStockDtoComponentsFromDbIfTheyExist(stockToUpdate))))
            .map(ModelMapper::mapStockToStockDto);

  }

  public StockDto specifyProduct(StockDto stockDto) {

    var productList = productRepository.findProductsByNameAndCategory(stockDto.getProductDto().getName(), ModelMapper.mapCategoryDtoToCategory(stockDto.getProductDto().getCategoryDto()))
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());

    stockDto.setProductDto( !productList.isEmpty() ? chooseAvailableProduct(productList) : getProductDtoIfValid(preciseProductDtoDetails(stockDto)));

    return stockDto;
  }


  private Optional<StockDto> getStockById(long stockId) {
    return stockRepository.findById(stockId)
            .map(ModelMapper::mapStockToStockDto);
  }

  public List<StockDto> getAllStocks() {

    return stockRepository.findAll()
            .stream()
            .map(ModelMapper::mapStockToStockDto)
            .collect(Collectors.toList());
  }

  public void deleteAllStocks() {
    stockRepository.deleteAll();
  }
}
