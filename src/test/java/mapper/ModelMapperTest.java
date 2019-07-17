package mapper;

import domain.*;
import domain.Error;
import domain.enums.EGuarantee;
import domain.enums.Epayment;
import dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("Test cases for ModelMapper")
class ModelMapperTest {


  @Test
  @DisplayName("map errorDto to Error")
  void test1() {

    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    ErrorDto errorDto = ErrorDto.builder().id(1L).date(localDateTime).message("ERROR").build();
    Error expectedError = Error.builder().id(1L).date(localDateTime).message("ERROR").build();

    //when
    Error actualError = ModelMapper.mapErrorDtoToError(errorDto);

    //then
    assertThat(actualError, equalTo(expectedError));

  }

  @Test
  @DisplayName("map error to errorDto")
  void test2() {

    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    Error error = Error.builder().id(1L).message("ERROR").date(localDateTime).build();
    ErrorDto expectedErrorDto = ErrorDto.builder().id(1L).date(localDateTime).message("ERROR").build();
    //when

    ErrorDto actualErrorDto = ModelMapper.mapErrorToErrorDto(error);
    //then

    assertThat(actualErrorDto, equalTo(expectedErrorDto));
  }

  @Test
  @DisplayName("map trade to tradeDto")
  void test3() {

    //given
    Trade trade = getExampleTrade();

    TradeDto expectedTradeDto = getExampleTradeDto();

    //when
    TradeDto actualTradeDto = ModelMapper.mapTradeToTradeDto(trade);

    //then
    assertThat(actualTradeDto, equalTo(expectedTradeDto));
  }

  @Test
  @DisplayName("map tradeDto to trade")
  void test4() {

    //given
    TradeDto tradeDto = getExampleTradeDto();
    Trade expectedTrade = getExampleTrade();
    //when
    Trade actualTrade = ModelMapper.mapTradeDtoToTrade(tradeDto);

    //then
    assertThat(actualTrade, equalTo(expectedTrade));
    assertThat(actualTrade.getProducers(), is(nullValue()));
  }

  @Test
  @DisplayName("map paymentDto to payment")
  void test5() {

    //given
    PaymentDto paymentDto = getExamplePaymentDto();
    Payment expectedPayment = getExamplePayment();
    //when
    Payment actualPayment = ModelMapper.mapPaymentDtoToPayment(paymentDto);

    //then
    assertThat(actualPayment, equalTo(expectedPayment));
    assertThat(actualPayment.getCustomerOrders(), is(nullValue()));
  }

  @Test
  @DisplayName("map payment to paymentDto")
  void test6() {

    //given
    Payment payment = getExamplePayment();
    PaymentDto expectedPaymentDto = getExamplePaymentDto();

    //when
    PaymentDto actualPaymentDto = ModelMapper.mapPaymentToPaymentDto(payment);

    //then
    assertThat(actualPaymentDto, equalTo(expectedPaymentDto));
  }

  @Test
  @DisplayName("map paymentDto to payment")
  void test7() {

    //given
    PaymentDto paymentDto = getExamplePaymentDto();
    Payment expectedPayment = getExamplePayment();

    //when
    Payment actualPayment = ModelMapper.mapPaymentDtoToPayment(paymentDto);

    //then
    assertThat(actualPayment, equalTo(expectedPayment));
    assertThat(actualPayment.getCustomerOrders(), is(nullValue()));
  }

  @Test
  @DisplayName("map category to categoryDto")
  void test8() {

    //given
    Category category = getExampleCategory();
    CategoryDto expectedCategoryDto = getExampleCategoryDto();

    //when
    CategoryDto actualCategoryDto = ModelMapper.mapCategoryToCategoryDto(category);

    //then
    assertThat(actualCategoryDto, equalTo(expectedCategoryDto));

  }

  @Test
  @DisplayName("map categoryDto to category")
  void test9() {

    //given
    CategoryDto categoryDto = CategoryDto.builder().id(1L).name("CATEGORY").build();
    Category expectedCategory = Category.builder().id(1L).name("CATEGORY").build();

    //when
    Category actualCategory = ModelMapper.mapCategoryDtoToCategory(categoryDto);

    //then
    assertThat(actualCategory, equalTo(expectedCategory));
  }

  @Test
  @DisplayName("map country to countryDto")
  void test10() {

    //given
    Country country = getExampleCountry();

    CountryDto expectedCountryDto = getExampleCountryDto();

    //when
    CountryDto actualCountryDto = ModelMapper.mapCountryToCountryDto(country);

    //then
    assertThat(actualCountryDto, equalTo(expectedCountryDto));

  }

  @Test
  @DisplayName("map countryDto to country")
  void test11() {

    //given
    CountryDto countryDto = getExampleCountryDto();
    Country expectedCountry = getExampleCountry();

    //when
    Country actualCountry = ModelMapper.mapCountryDtoToCountry(countryDto);

    //then
    assertThat(actualCountry, equalTo(expectedCountry));

  }

  @Test
  @DisplayName("map customer to customerDto")
  void test12() {

    //given
    Customer customer = getExampleCustomer();
    CustomerDto expectedCustomerDto = getExampleCustomerDto();

    //when
    CustomerDto actualCustomerDto = ModelMapper.mapCustomerToCustomerDto(customer);

    //then
    assertThat(expectedCustomerDto, equalTo(actualCustomerDto));
  }

  @Test
  @DisplayName("map customerDto to customer")
  void test13() {

    //given
    CustomerDto customerDto = getExampleCustomerDto();
    Customer expectedCustomer = getExampleCustomer();

    //when
    Customer actualCustomer = ModelMapper.mapCustomerDtoToCustomer(customerDto);

    //then
    assertThat(actualCustomer, equalTo(expectedCustomer));
    assertThat(actualCustomer.getCustomerOrders(), is(nullValue()));
  }

  @Test
  @DisplayName("map producer to producerDto")
  void test14() {

    //given
    Producer producer = getExampleProducer();
    ProducerDto expectedProducerDto = getExampleProducerDto();

    //when
    ProducerDto actualProducerDto = ModelMapper.mapProducerToProducerDto(producer);

    //then
    assertThat(actualProducerDto, equalTo(expectedProducerDto));

  }

  @Test
  @DisplayName("map producerDto to producer")
  void test15() {

    //given
    ProducerDto producerDto = getExampleProducerDto();
    Producer expectedProducer = getExampleProducer();

    //when
    Producer actualProducer = ModelMapper.mapProducerDtoToProducer(producerDto);

    //then
    assertThat(actualProducer, equalTo(expectedProducer));
    assertThat(actualProducer.getProductList(), is(nullValue()));

  }

  @Test
  @DisplayName("map shop to shopDto")
  void test16() {

    //given
    Shop shop = getExampleShop();
    ShopDto expectedShopDto = getExampleShopDto();

    //when
    ShopDto actualShopDto = ModelMapper.mapShopToShopDto(shop);

    //then
    assertThat(actualShopDto, is(equalTo(expectedShopDto)));
  }

  @Test
  @DisplayName("map shopDto to shop")
  void test17() {

    //given
    ShopDto shopDto = getExampleShopDto();
    Shop expectedShop = getExampleShop();

    //when

    Shop actualShop = ModelMapper.mapShopDtoToShop(shopDto);
    //then

    assertThat(actualShop, is(equalTo(expectedShop)));
  }

  @Test
  @DisplayName("map stock to stockDto")
  void test18() {

    //given
    Stock stock = getExampleStock();
    StockDto expectedStockDto = getExampleStockDto();

    //when
    StockDto actualStockDto = ModelMapper.mapStockToStockDto(stock);

    //then
    assertThat(actualStockDto, is(equalTo(expectedStockDto)));

  }

  @Test
  @DisplayName("map product to productDto")
  void test19() {

    //given
    Product product = getExampleProduct();
    ProductDto expectedProductDto = getExampleProductDto();

    //when
    ProductDto actualProductDto = ModelMapper.mapProductToProductDto(product);

    //then
    assertThat(actualProductDto, is(equalTo(expectedProductDto)));
  }

  @Test
  @DisplayName("map productDto to product")
  void test20() {

    //given
    ProductDto productDto = getExampleProductDto();
    Product expectedProduct = getExampleProduct();

    //when
    Product actualProduct = ModelMapper.mapProductDtoToProduct(productDto);

    //then
    assertThat(actualProduct, is(equalTo(expectedProduct)));
  }

  @Test
  @DisplayName("map customerOrder to customerOrderDto")
  void test21() {

    //given
    CustomerOrder customerOrder = getExampleCustomerOrder();
    CustomerOrderDto expectedCustomerOrderDto = getExampleCustomerOrderDto();

    //when
    CustomerOrderDto actualCustomerOrderDto = ModelMapper.mapCustomerOrderToCustomerOrderDto(customerOrder);

    //then
    assertThat(actualCustomerOrderDto, is(equalTo(expectedCustomerOrderDto)));

  }

  @Test
  @DisplayName("map customerOrderDto to customerOrder")
  void test22() {

    //given
    CustomerOrderDto customerOrderDto = getExampleCustomerOrderDto();
    CustomerOrder expectedCustomerOrder = getExampleCustomerOrder();

    //when
    CustomerOrder actualCustomerOrder = ModelMapper.mapCustomerOrderDtoToCustomerOrder(customerOrderDto);

    //then
    assertThat(actualCustomerOrder, is(equalTo(expectedCustomerOrder)));
  }

  private Trade getExampleTrade() {
    return Trade.builder()
            .id(1L)
            .name("TRADE")
            .producers(getProducersList())
            .build();
  }

  private TradeDto getExampleTradeDto() {

    return TradeDto.builder()
            .id(1L)
            .name("TRADE")
            .build();
  }

  private Payment getExamplePayment() {
    return Payment.builder()
            .id(1L)
            .epayment(Epayment.CASH)
            .customerOrders(getExampleCustomerOrders())
            .build();
  }


  private PaymentDto getExamplePaymentDto() {

    return PaymentDto.builder()
            .id(1L)
            .epayment(Epayment.CASH)
            .build();
  }


  private Category getExampleCategory() {

    return Category.builder()
            .id(1L)
            .name("CATEGORY")
            .products(getProductsList())
            .build();

  }

  private CategoryDto getExampleCategoryDto() {

    return CategoryDto.builder()
            .id(1L)
            .name("CATEGORY")
            .build();
  }

  private Country getExampleCountry() {

    return Country.builder()
            .id(1L)
            .name("COUNTRY")
            .customers(getCustomerList())
            .producers(getProducersList())
            .shop(getShopSet())
            .build();
  }

  private CountryDto getExampleCountryDto() {

    return CountryDto.builder()
            .id(1L)
            .name("COUNTRY")
            .build();
  }

  private Customer getExampleCustomer() {

    return Customer.builder()
            .id(1L)
            .name("NAME")
            .surname("SURNAME")
            .age(30)
            .country(getExampleCountry())
            .customerOrders(getExampleCustomerOrders())
            .build();
  }

  private CustomerDto getExampleCustomerDto() {

    return CustomerDto.builder()
            .id(1L)
            .name("NAME")
            .surname("SURNAME")
            .age(30)
            .countryDto(getExampleCountryDto())
            .build();
  }

  private Producer getExampleProducer() {

    return Producer.builder()
            .id(1L)
            .name("PRODUCER")
            .country(getExampleCountry())
            .trade(getExampleTrade())
            .productList(getProductsList())
            .build();
  }

  private ProducerDto getExampleProducerDto() {

    return ProducerDto.builder()
            .id(1L)
            .name("PRODUCER")
            .country(getExampleCountryDto())
            .trade(getExampleTradeDto())
            .build();
  }

  private Product getExampleProduct() {

    return Product.builder()
            .id(1L)
            .name("PRODUCT")
            .producer(getExampleProducer())
            .guaranteeComponents(getExampleGuaranteeComponents())
            .price(new BigDecimal("300"))
            .category(getExampleCategory())
            .customerOrders(getExampleCustomerOrders())
            .stocks(getStockList())
            .build();
  }

  private ProductDto getExampleProductDto() {

    return ProductDto.builder()
            .id(1L)
            .name("PRODUCT")
            .producerDto(getExampleProducerDto())
            .guaranteeComponents(getExampleGuaranteeComponents())
            .price(new BigDecimal("300"))
            .categoryDto(getExampleCategoryDto())
            .build();
  }

  private Shop getExampleShop() {

    return Shop.builder()
            .id(1L)
            .name("SHOP")
            .country(getExampleCountry())
            .stocks(getStockList())
            .build();
  }

  private ShopDto getExampleShopDto() {

    return ShopDto.builder()
            .id(1L)
            .name("SHOP")
            .countryDto(getExampleCountryDto())
            .build();
  }

  private Stock getExampleStock() {

    return Stock.builder()
            .id(1L)
            .product(getExampleProduct())
            .shop(getExampleShop())
            .quantity(30)
            .build();
  }

  private StockDto getExampleStockDto() {

    return StockDto.builder()
            .id(1L)
            .productDto(getExampleProductDto())
            .shopDto(getExampleShopDto())
            .quantity(30)
            .build();
  }

  private CustomerOrder getExampleCustomerOrder() {

    return CustomerOrder.builder()
            .id(1L)
            .date(LocalDate.of(2020, 10, 20))
            .customer(getExampleCustomer())
            .product(getExampleProduct())
            .payment(getExamplePayment())
            .discount(new BigDecimal("0.1"))
            .quantity(10)
            .build();
  }

  private CustomerOrderDto getExampleCustomerOrderDto() {

    return CustomerOrderDto.builder()
            .id(1L)
            .date(LocalDate.of(2020, 10, 20))
            .customer(getExampleCustomerDto())
            .product(getExampleProductDto())
            .payment(getExamplePaymentDto())
            .discount(new BigDecimal("0.1"))
            .quantity(10)
            .build();
  }

  private List<Stock> getStockList() {
    return List.of(Stock.builder()
            .id(1L)
            .product(Product.builder()
                    .id(1L).build())
            .quantity(30)
            .shop(Shop.builder().id(1L).build())
            .build());
  }

  private Set<Shop> getShopSet() {
    return Set.of(
            Shop.builder().id(1L).build(),
            Shop.builder().id(2L).build());
  }

  private List<Customer> getCustomerList() {
    return List.of(
            Customer.builder().id(1L).build(),
            Customer.builder().id(2L).build()
    );

  }

  private List<Product> getProductsList() {
    return List.of(
            Product.builder().id(1L).build(),
            Product.builder().id(2L).build()
    );
  }

  private List<CustomerOrder> getExampleCustomerOrders() {
    return List.of(
            CustomerOrder.builder().id(1L).build(),
            CustomerOrder.builder().id(2L).build()
    );
  }

  private List<Producer> getProducersList() {
    return List.of(
            Producer.builder().id(1L).build(),
            Producer.builder().id(2L).build()
    );
  }

  private List<EGuarantee> getExampleGuaranteeComponents() {
    return List.of(EGuarantee.SERVICE, EGuarantee.EXCHANGE);
  }

}
