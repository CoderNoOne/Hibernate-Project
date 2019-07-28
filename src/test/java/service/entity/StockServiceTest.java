package service.entity;

import domain.*;
import dto.ProducerDto;
import dto.StockDto;
import exception.AppException;
import mapper.ModelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@Tag("Services")
@DisplayName("Test cases for StockService")
class StockServiceTest {

  @Mock
  private TradeRepository tradeRepository;

  @Mock
  private ProducerRepository producerRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private StockRepository stockRepository;

  @Mock
  private ShopRepository shopRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CountryRepository countryRepository;

  @InjectMocks
  private StockService stockService;

  @Test
  @DisplayName("update stock: stock quantity")
  void test1() {

    //given
    StockDto stockDtoToUpdate = StockDto.builder()
            .id(2L)
            .quantity(10)
            .build();

    Optional<Stock> stockFromDb = Optional.of(Stock.builder()
            .id(2L)
            .quantity(30)
            .product(Product.builder()
                    .id(1L)
                    .producer(Producer.builder()
                            .id(3L)
                            .name("PRODUCER")
                            .country(Country.builder()
                                    .id(2L)
                                    .name("COUNTRY")
                                    .build())
                            .trade(Trade.builder()
                                    .name("TRADE")
                                    .build())
                            .build())
                    .category(Category.builder()
                            .id(1L)
                            .name("CATEGORY")
                            .build())
                    .name("PRODUCT")
                    .price(new BigDecimal("300"))
                    .build())
            .shop(Shop.builder()
                    .name("SHOP")
                    .country(Country.builder()
                            .id(2L)
                            .name("COUNTRY")
                            .build())
                    .build())
            .build());


    Stock stockToUpdate = Stock.builder()
            .id(2L)
            .quantity(10)
            .product(stockFromDb.get().getProduct())
            .shop(stockFromDb.get().getShop())
            .build();

    Optional<Stock> expectedUpdatedStock = Optional.of(
            Stock.builder()
                    .id(2L)
                    .quantity(10)
                    .product(Product.builder()
                            .id(1L)
                            .producer(Producer.builder()
                                    .id(3L)
                                    .country(Country.builder()
                                            .id(2L)
                                            .name("COUNTRY")
                                            .build())
                                    .build())
                            .category(Category.builder()
                                    .id(1L)
                                    .name("CATEGORY")
                                    .build())
                            .name("PRODUCT")
                            .price(new BigDecimal("300"))
                            .build())
                    .shop(Shop.builder()
                            .id(1L)
                            .name("SHOP")
                            .country(Country.builder()
                                    .id(2L)
                                    .name("COUNTRY")
                                    .build())
                            .build())
                    .build());

    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Stock> argumentCaptorStock = ArgumentCaptor.forClass(Stock.class);
    ArgumentCaptor<String> countryNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

    given(stockRepository.findById(argumentCaptor.capture()))
            .willReturn(stockFromDb);

    given(stockRepository.addOrUpdate(argumentCaptorStock.capture()))
            .willReturn(expectedUpdatedStock);

    given(shopRepository.findById(1L))
            .willReturn(Optional.of(Shop.builder()
                    .name("SHOP")
                    .country(Country.builder()
                            .id(2L)
                            .name("COUNTRY")
                            .build())
                    .build()));

    given(productRepository.findByNameAndCategoryAndProducer("PRODUCT",
            Category.builder().id(1L).name("CATEGORY").build()
            , Producer.builder()
                    .name("PRODUCER")
                    .id(3L)
                    .country(Country.builder()
                            .id(2L)
                            .name("COUNTRY")
                            .build())
                    .build()))
            .willReturn(Optional.of(Product.builder()
                    .id(1L)
                    .producer(Producer.builder()
                            .id(3L)
                            .country(Country.builder()
                                    .id(2L)
                                    .name("COUNTRY")
                                    .build())
                            .trade(Trade.builder()
                                    .id(1L)
                                    .name("TRADE")
                                    .build())
                            .build())
                    .category(Category.builder()
                            .id(1L)
                            .name("CATEGORY")
                            .build())
                    .name("PRODUCT")
                    .price(new BigDecimal("300"))
                    .build()));

    given(producerRepository.findByNameAndTradeAndCountry("PRODUCER", Trade.builder().id(1L).name("TRADE")
            .build(), Country.builder()
            .id(2L)
            .name("COUNTRY")
            .build())).willReturn(Optional.empty());

    given(categoryRepository.findCategoryByName("CATEGORY")).willReturn(Optional.empty());

    given(countryRepository.findCountryByName(countryNameArgumentCaptor.capture()))
            .willReturn(Optional.empty());

    given(tradeRepository.findTradeByName("TRADE")).willReturn(Optional.empty());

    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<StockDto> actualStockDto = stockService.updateStock(stockDtoToUpdate);
      assertThat(actualStockDto, is(equalTo(expectedUpdatedStock.map(ModelMapper::mapStockToStockDto))));
    });

    InOrder inOrder = inOrder(stockRepository);
    inOrder.verify(stockRepository, times(1)).findById(stockDtoToUpdate.getId());
    inOrder.verify(stockRepository, times(1)).addOrUpdate(stockToUpdate);

    then(tradeRepository).should(times(1)).findTradeByName(stockToUpdate.getProduct().getProducer().getTrade().getName());
    then(countryRepository).should(times(2)).findCountryByName(stockToUpdate.getProduct().getProducer().getCountry().getName());
    then(producerRepository).should(times(1)).findByNameAndTradeAndCountry("PRODUCER", Trade.builder().name("TRADE").build(), Country.builder().id(2L).name("COUNTRY").build());
    then(productRepository).should(times(1)).findByNameAndCategoryAndProducer("PRODUCT", Category.builder().id(1L).name("CATEGORY").build(), Producer.builder().id(3L).name("PRODUCER").country(Country.builder().id(2L).name("COUNTRY").build()).trade(Trade.builder().name("TRADE").build()).build());

    then(shopRepository).should(times(1)).findShopByNameAndCountry("SHOP", "COUNTRY");
  }

  @Test
  @DisplayName("getProducersWithTradeAndNumberOfProductsProducedGreaterThan method: trade name is null ")
  void test2() {

    //given
    String tradeName = null;
    Integer minAmountOfProducts = 2;
    String expectedExceptionMessage = "Trade name is null";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> stockService.getProducersWithTradeAndNumberOfProductsProducedGreaterThan(tradeName, minAmountOfProducts));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(stockRepository).should(never()).findStocksWithProducerTradeName(anyString());
  }

  @Test
  @DisplayName("getProducerWithTradeAndNumberOfProductsProducedGreaterThan method: minAmountOfProducts is null")
  void test3() {

    //given
    String tradeName = "TRADE";
    Integer minAmountOfProducts = null;
    String expectedExceptionMessage = "Minimum number of products is null";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> stockService.getProducersWithTradeAndNumberOfProductsProducedGreaterThan(tradeName, minAmountOfProducts));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(stockRepository).should(never()).findStocksWithProducerTradeName(anyString());

  }

  @Test
  @DisplayName("getProducerWithTradeAndNumberOfProductsProducedGreaterThan method : valid arguments passed")
  void test4() {

    //given
    ArgumentCaptor<String> tradeNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
    String tradeName = "TRADE";
    Integer minAmountOfProducts = 2;
    List<Stock> stockList = List.of(
            Stock.builder()
                    .id(1L)
                    .quantity(1)
                    .shop(Shop.builder().name("SHOP").build())
                    .product(Product.builder().name("PRODUCT")
                            .producer(Producer.builder().name("PRODUCER UNO").build())
                            .build())
                    .build(),

            Stock.builder()
                    .id(2L)
                    .shop(Shop.builder().name("SHOP DUE").build())
                    .product(Product.builder().name("PRODUCT DUE")
                            .producer(Producer.builder()
                                    .name("PRODUCER DUE")
                                    .build())
                            .build())
                    .quantity(10)
                    .build());

    given(stockRepository.findStocksWithProducerTradeName(tradeName)).willReturn(stockList);

    //when
    //then

    assertDoesNotThrow(() -> {
      List<ProducerDto> actualResultList = stockService.getProducersWithTradeAndNumberOfProductsProducedGreaterThan(tradeName, minAmountOfProducts);
      assertThat(actualResultList, hasSize(1));
      assertThat(actualResultList.get(0), is(equalTo(ProducerDto.builder().name("PRODUCER DUE").build())));
    });
    then(stockRepository).should(times(1)).findStocksWithProducerTradeName(tradeNameArgumentCaptor.capture());
    assertThat(tradeName, is(equalTo(tradeNameArgumentCaptor.getValue())));

  }

  @Test
  @DisplayName("getAllStocks method")
  void test5() {

    //given
    List<Stock> stockList = List.of(
            Stock.builder()
                    .id(1L)
                    .quantity(1)
                    .shop(Shop.builder().name("SHOP").build())
                    .product(Product.builder().name("PRODUCT")
                            .producer(Producer.builder().name("PRODUCER UNO").build())
                            .build())
                    .build(),

            Stock.builder()
                    .id(2L)
                    .shop(Shop.builder().name("SHOP DUE").build())
                    .product(Product.builder().name("PRODUCT DUE")
                            .producer(Producer.builder()
                                    .name("PRODUCER DUE")
                                    .build())
                            .build())
                    .quantity(10)
                    .build());

    List<StockDto> expecetedResultList = stockList
            .stream()
            .map(ModelMapper::mapStockToStockDto)
            .collect(Collectors.toList());

    given(stockRepository.findAll()).willReturn(stockList);


    //when
    //then
    assertDoesNotThrow(() -> {
      List<StockDto> actualStockList = stockService.getAllStocks();
      assertThat(actualStockList.size(), is(equalTo(2)));
      assertThat(actualStockList, is(equalTo(expecetedResultList)));
    });

    then(stockRepository).should(times(1)).findAll();
  }
}
