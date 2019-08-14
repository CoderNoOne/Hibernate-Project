package service.entity;

import domain.Stock;
import dto.ProducerDto;
import dto.ProductDto;
import dto.ShopDto;
import dto.StockDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.util.*;
import java.util.stream.Collectors;

import static util.entity_utils.StockUtil.getStockDtoIfValid;

@RequiredArgsConstructor
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

  private StockDto setShop(StockDto stockDto) {

    Stock stock = ModelMapper.mapStockDtoToStock(stockDto);

    shopRepository.findShopByNameAndCountry(stock.getShop().getName(), stock.getShop().getCountry().getName())
            .ifPresentOrElse(stock::setShop, () -> {
              countryRepository.findCountryByName(stock.getShop().getCountry().getName())
                      .ifPresentOrElse(country -> stock.getShop().setCountry(country),
                              () -> countryRepository.add(stock.getShop().getCountry()));
              shopRepository.add(stock.getShop());
            });
    return ModelMapper.mapStockToStockDto(stock);
  }

  private Optional<StockDto> addStockToDb(StockDto stockDto) {

    return stockRepository
            .add(ModelMapper.mapStockDtoToStock(setProduct(setProducer(setCategory(setShop(stockDto))))))
            .map(ModelMapper::mapStockToStockDto);

  }

  private StockDto setProduct(StockDto stockDto) {

    Stock stock = ModelMapper.mapStockDtoToStock(stockDto);

    productRepository.findByNameAndCategoryAndProducer(stock.getProduct().getName(), stock.getProduct().getCategory(), stock.getProduct().getProducer())
            .ifPresentOrElse(stock::setProduct, () -> productRepository.add(stock.getProduct()));
    return ModelMapper.mapStockToStockDto(stock);

  }

  private StockDto setProducer(StockDto stockDto) {

    Stock stock = ModelMapper.mapStockDtoToStock(stockDto);

    producerRepository.findByNameAndTradeAndCountry(stock.getProduct().getProducer().getName(), stock.getProduct().getProducer().getTrade(),
            stock.getProduct().getProducer().getCountry())
            .ifPresentOrElse(producer -> stock.getProduct().setProducer(producer),
                    () -> {
                      tradeRepository.findTradeByName(stock.getProduct().getProducer().getTrade().getName())
                              .ifPresentOrElse(trade -> stock.getProduct().getProducer().setTrade(trade),
                                      () -> tradeRepository.add(stock.getProduct().getProducer().getTrade()));
                      countryRepository.findCountryByName(stock.getProduct().getProducer().getCountry().getName())
                              .ifPresentOrElse(country -> stock.getProduct().getProducer().setCountry(country),
                                      () -> countryRepository.add(stock.getProduct().getProducer().getCountry()));
                      producerRepository.add(stock.getProduct().getProducer());
                    });
    return ModelMapper.mapStockToStockDto(stock);
  }

  private StockDto setCategory(StockDto stockDto) {

    Stock stock = ModelMapper.mapStockDtoToStock(stockDto);

    categoryRepository.findCategoryByName(stock.getProduct().getCategory().getName())
            .ifPresentOrElse(category -> stock.getProduct().setCategory(category),
                    () -> categoryRepository.add(stock.getProduct().getCategory()));
    return ModelMapper.mapStockToStockDto(stock);
  }

  public void addStockToDbFromUserInput(StockDto stockDto) {

    if (stockDto == null) {
      throw new AppException("StockDto object is null");
    }

    getStockByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto())
            .ifPresentOrElse(stock -> increaseStockQuantity(stock.getId(), stock.getQuantity()),
                    () -> addStockToDb(stockDto));
  }


  private StockDto setStockDtoComponentsFromDbIfTheyExist(StockDto stockDto) {

    return StockDto.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .shopDto(setShopComponentFromDb(shopRepository.findShopByNameAndCountry(
                    stockDto.getShopDto().getName(),
                    stockDto.getShopDto().getCountryDto().getName())
                    .map(ModelMapper::mapShopToShopDto)
                    .orElseGet(() -> stockDto.getShopDto())))
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

  public void decreaseStockQuantity(ShopDto shopDto, ProductDto productDto, Integer quantity) {

    stockRepository
            .findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto))
            .ifPresentOrElse(stock -> stockRepository.decreaseStockQuantity(stock, quantity),
                    () -> {
                      throw new AppException("Stock with shop:" + shopDto.getName() + "and product: " + productDto.getName() + " doesn't exist!");
                    });
  }

  private void increaseStockQuantity(Long stockId, Integer quantity) {

    if (stockId == null) {
      throw new AppException("Stock id is null");
    }

    if (quantity == null) {
      throw new AppException("Stock quantity is null");
    }

    stockRepository.increaseStockQuantity(stockId, quantity);
  }

  private Optional<StockDto> getStockByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository
            .findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto))
            .map(ModelMapper::mapStockToStockDto);
  }

  public List<ProducerDto> getProducersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts) {

    if (tradeName == null) {
      throw new AppException("Trade name is null");
    }

    if (minAmountOfProducts == null) {
      throw new AppException("Minimum number of products is null");
    }

    return stockRepository.findStocksWithProducerTradeName(tradeName)
            .stream().collect(Collectors.collectingAndThen(
                    Collectors.groupingBy(stock -> stock.getProduct().getProducer(), Collectors.summingInt(Stock::getQuantity)),
                    map -> map.entrySet().stream().filter(e -> e.getValue() > minAmountOfProducts).map(Map.Entry::getKey)
                            .map(ModelMapper::mapProducerToProducerDto)
                            .collect(Collectors.toList())));
  }

  public Optional<StockDto> updateStock(StockDto stockDtoToUpdate) {

    if (stockDtoToUpdate == null) {
      throw new AppException("StockDto object is null");
    }

    if (stockDtoToUpdate.getId() == null) {
      throw new AppException("Stock id is null - Stock is not in db yet");
    }

    Stock stockFromDb = stockRepository.findById(stockDtoToUpdate.getId())
            .orElseThrow(() -> new AppException("Stock with id: " + stockDtoToUpdate.getId() + " doesn't exist in DB yet"));

    StockDto stockToUpdate = StockDto.builder()
            .id(stockDtoToUpdate.getId())
            .quantity(stockDtoToUpdate.getQuantity() != null ? stockDtoToUpdate.getQuantity() : stockFromDb.getQuantity())
            .productDto(ModelMapper.mapProductToProductDto(stockFromDb.getProduct()))
            .shopDto(ModelMapper.mapShopToShopDto(stockFromDb.getShop()))
            .build();

    return stockRepository
            .add(ModelMapper.mapStockDtoToStock(getStockDtoIfValid(setStockDtoComponentsFromDbIfTheyExist(stockToUpdate))))
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

  public Map<ProductDto, Set<ShopDto>> getProductDtoAndShopWithQuantityInStockMoreThan(List<ProductDto> productDtos, Integer quantity) {

    return getAllStocks().stream()
            .filter(stockDto -> productDtos.contains(stockDto.getProductDto()))
            .collect(Collectors.collectingAndThen(Collectors.groupingBy(StockDto::getProductDto
                    , Collectors.collectingAndThen(Collectors.groupingBy(StockDto::getShopDto, Collectors.summingInt(StockDto::getQuantity)),
                            innerMap -> innerMap.entrySet()
                                    .stream()
                                    .filter(innerEntry -> innerEntry.getValue() >= quantity)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toSet()))),
                    map -> map.entrySet()
                            .stream()
                            .filter(e -> !e.getValue().isEmpty())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue))));
  }
}
