package service.entity;

import domain.*;
import domain.enums.EGuarantee;
import domain.enums.Epayment;
import dto.*;
import exception.AppException;
import mapper.ModelMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CustomerOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Tag("Services")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@DisplayName("Test cases for CustomerOrderService")
class CustomerOrderServiceTest {


  @Mock
  private CustomerOrderRepository customerOrderRepository;

  @InjectMocks
  private CustomerOrderService customerOrderService;


  @Test
  @DisplayName("getAll customerOrders method")
  void test1() {

    //given
    List<CustomerOrder> customerOrders = Arrays.asList(CustomerOrder.builder().id(1L).build(), CustomerOrder.builder().id(2L).build());

    given(customerOrderRepository.findAll()).willReturn(customerOrders);

    //when
    List<CustomerOrderDto> allCustomerOrders = customerOrderService.getAllCustomerOrders();
    System.out.println(allCustomerOrders);

    //then
    assertThat(allCustomerOrders, allOf(hasSize(2), notNullValue()));
    assertThat(allCustomerOrders.get(0).getId(), equalTo(1L));
    assertThat(allCustomerOrders.get(1).getId(), equalTo(2L));

    then(customerOrderRepository).should(times(1)).findAll();
  }

  @Test
  @DisplayName("getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountry method")
  void test2() {

    //given

    List<CustomerOrder> customerOrderList = List.of(
            CustomerOrder.builder()
                    .id(1L)
                    .customer(Customer.builder()
                            .country(Country.builder()
                                    .name("COUNTRY")
                                    .build())
                            .id(1L)
                            .name("NAME")
                            .surname("SURNAME")
                            .age(30)
                            .build())
                    .quantity(10)
                    .discount(new BigDecimal("0.2"))
                    .payment(Payment.builder()
                            .epayment(Epayment.CASH)
                            .build())
                    .product(Product.builder()
                            .price(new BigDecimal("300"))
                            .name("PRODUCT NAME")
                            .producer(Producer.builder()
                                    .country(Country.builder()
                                            .name("COUNTRY")
                                            .build())
                                    .build())
                            .build())
                    .build()


    );


    given(customerOrderRepository.findAll())
            .willReturn(customerOrderList);

    //when
    //then
    Map<CustomerDto, Long> resultMap = customerOrderService.getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim();

    assertThat(resultMap.size(), Matchers.is(1));

    assertThat(resultMap,
            hasEntry(CustomerDto.builder()
                            .id(1L)
                            .name("NAME")
                            .surname("SURNAME")
                            .age(30)
                            .countryDto(CountryDto.builder()
                                    .name("COUNTRY")
                                    .build())
                            .build(),
                    0L));

  }

  @Test
  @DisplayName("getProductsOrderedByCustomerGroupedByProducer - customerName null")
  void test3() {

    //given
    AppException appException1 = new AppException("getProductsOrderedByCustomerGroupedByProducer - not valid input data");

    //when
    //then
    AppException appException2 = assertThrows(AppException.class,
            () -> customerOrderService.getProductsOrderedByCustomerGroupedByProducer(null, "SMITH", "ENGLAND"));

    assertThat("Exception message", appException1.getMessage(), equalTo(appException2.getMessage()));
  }

  @Test
  @DisplayName("getProductsOrderedByCustomerGroupedByProducer - null argument")
  void test4() {

    //given
    String expectedExceptionMessage = "getProductsOrderedByCustomerGroupedByProducer - not valid input data";

    //when
    //then
    AppException appException = assertThrows(AppException.class,
            () -> customerOrderService.getProductsOrderedByCustomerGroupedByProducer(null, "SMITH", "ENGLAND"));

    assertThat("Exception message", appException.getMessage(), equalTo(expectedExceptionMessage));

  }

  @Test
  @DisplayName("getProductsOrderedByCustomerGroupedByProducer - proper argument should not throw an exception")
  void test5() {

    // TODO: 2019-07-29
    //given
    Customer customer = Customer.builder()
            .id(1L)
            .name("JOHN")
            .surname("SMITH")
            .age(30)
            .country(Country.builder()
                    .id(1L)
                    .name("USA")
                    .build())
            .build();
    List<CustomerOrder> returnValue = List.of(
            CustomerOrder.builder()
                    .id(1L)
                    .quantity(10)
                    .customer(Customer.builder()
                            .id(1L)
                            .name("JOHN")
                            .surname("SMITH")
                            .age(30)
                            .country(Country.builder()
                                    .id(1L)
                                    .name("USA")
                                    .build())
                            .build())
                    .date(LocalDate.of(2020, 10, 20))
                    .discount(new BigDecimal("0.2"))
                    .payment(Payment.builder()
                            .id(1L)
                            .epayment(Epayment.CARD)
                            .build())
                    .product(Product.builder()
                            .id(1L)
                            .name("PRODUCT ONE")
                            .price(new BigDecimal("500"))
                            .producer(Producer.builder()
                                    .id(1L)
                                    .name("PRODUCER ONE")
                                    .build())
                            .build())
                    .build(),

            CustomerOrder.builder()
                    .id(2L)
                    .quantity(30)
                    .customer(customer)
                    .date(LocalDate.of(2019, 6, 14))
                    .discount(new BigDecimal("0.1"))
                    .payment(Payment.builder()
                            .id(2L)
                            .epayment(Epayment.CASH)
                            .build())
                    .product(Product.builder()
                            .id(2L)
                            .name("PRODUCT TWO")
                            .price(new BigDecimal("800"))
                            .producer(Producer.builder()
                                    .id(2L)
                                    .name("PRODUCER TWO")
                                    .build())
                            .build())
                    .build()

    );
    given(customerOrderRepository.findProductsOrderedByCustomer(customer.getName(), customer.getSurname(), customer.getCountry().getName()))
            .willReturn(returnValue);

    Map<ProducerDto, List<ProductDto>> expectedResultMap = Map.of(
            ProducerDto.builder()
                    .id(1L)
                    .name("PRODUCER ONE")
                    .build(),
            List.of(
                    ProductDto.builder()
                            .id(1L)
                            .name("PRODUCT ONE")
                            .price(new BigDecimal("500"))
                            .producerDto(ProducerDto.builder()
                                    .id(1L)
                                    .name("PRODUCER ONE")
                                    .build())
                            .build()
            ),
            ProducerDto.builder()
                    .id(2L)
                    .name("PRODUCER TWO")
                    .build(),
            List.of(
                    ProductDto.builder()
                            .id(2L)
                            .name("PRODUCT TWO")
                            .price(new BigDecimal("800"))
                            .producerDto(ProducerDto.builder()
                                    .id(2L)
                                    .name("PRODUCER TWO")
                                    .build())
                            .build()
            )
    );

    //when
    //then
    assertDoesNotThrow(() -> {
      Map<ProducerDto, List<ProductDto>> actualResult = customerOrderService.getProductsOrderedByCustomerGroupedByProducer(customer.getName(), customer.getSurname(), customer.getCountry().getName());
      assertThat(actualResult, is(equalTo(expectedResultMap)));
    });

    then(customerOrderRepository).should(times(1)).findProductsOrderedByCustomer(customer.getName(), customer.getSurname(), customer.getCountry().getName());
  }

  @Test
  @DisplayName("getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder")
  void test6() {

    //given
    String countryName = null;
    Integer minAge = 20;
    Integer maxAge = 30;
    String expectedExceptionMessage = String.format("At least one argument is null (countryName: %s minAge %d maxAge: %d", countryName, minAge, maxAge);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> customerOrderService.getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder(countryName, minAge, maxAge));
    assertThat(appException.getMessage(), Matchers.is(equalTo(expectedExceptionMessage)));
    then(customerOrderRepository).should(never()).findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder: case -> minAge is greater than maxAge")
  void test7() {

    //given
    String countryName = "USA";
    Integer minAge = 30;
    Integer maxAge = 20;
    String expectedExceptionMessage = String.format("Min age: %d is greater than %d", minAge, maxAge);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> customerOrderService.getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder(countryName, minAge, maxAge));
    assertThat(appException.getMessage(), Matchers.is(equalTo(expectedExceptionMessage)));
    then(customerOrderRepository).should(never()).findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder: case -> proper input values")
  void test8() {

    //given
    String countryName = "USA";
    Integer minAge = 20;
    Integer maxAge = 300;

    ArgumentCaptor<String> countryNameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Integer> minAgeCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> maxAgeCaptor = ArgumentCaptor.forClass(Integer.class);

    List<Product> repositoryMethodResultList = List.of(

            Product.builder()
                    .id(1L)
                    .price(new BigDecimal("20"))
                    .name("PRODUCT TWO")
                    .build(),

            Product.builder()
                    .id(2L)
                    .price(new BigDecimal("300"))
                    .name("PRODUCT ONE")
                    .build()


    );
    given(customerOrderRepository.findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(countryNameCaptor.capture(), minAgeCaptor.capture(), maxAgeCaptor.capture()))
            .willReturn(repositoryMethodResultList);

    //when
    //then
    assertDoesNotThrow(() -> {
      List<ProductDto> actualProductDtoList = customerOrderService.getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder(countryName, minAge, maxAge);
      assertThat(actualProductDtoList, is(equalTo(repositoryMethodResultList.stream().map(ModelMapper::mapProductToProductDto).sorted(Comparator.comparing(ProductDto::getPrice).reversed()).collect(Collectors.toList()))));
      assertThat(actualProductDtoList.get(0).getPrice(), is(equalTo(new BigDecimal("300"))));
      assertThat(actualProductDtoList.get(1).getPrice(), is(equalTo(new BigDecimal("20"))));
    });

    then(customerOrderRepository).should(times(1)).findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange("USA", 20, 300);
  }


  @Test
  @DisplayName("getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory: argument object is null. Method should throw an exception")
  void test9() {

    //given
    String expectedExceptionMessage = "guaranteeComponents collection object is null";
    Set<EGuarantee> guaranteeComponents = null;

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> customerOrderService.getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(null));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(customerOrderRepository).should(never()).findProductsWithActiveWarranty();
  }

  @Test
  @DisplayName("getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory: valid argument - exception shouldn't be thrown")
  void test10() {

    //given
    Set<EGuarantee> guaranteeComponents = Set.of(EGuarantee.SERVICE, EGuarantee.HELP_DESK);
    Map<String, List<ProductDto>> expectedResult = Map.of(

            "CATEGORY ONE", List.of(
                    ProductDto.builder()
                            .id(1L)
                            .name("PRODUCT ONE")
                            .categoryDto(CategoryDto.builder()
                                    .name("CATEGORY ONE")
                                    .build())
                            .guaranteeComponents(List.of(
                                    EGuarantee.SERVICE
                            ))
                            .build()),

            "CATEGORY TWO", List.of(ProductDto.builder()
                    .id(2L)
                    .name("PRODUCT TWO")
                    .categoryDto(CategoryDto.builder()
                            .name("CATEGORY TWO")
                            .build())
                    .guaranteeComponents(List.of(
                            EGuarantee.HELP_DESK
                    ))
                    .build()),

            "CATEGORY THREE", List.of(
                    ProductDto.builder()
                            .name("PRODUCT THREE")
                            .id(3L)
                            .categoryDto(CategoryDto.builder()
                                    .name("CATEGORY THREE")
                                    .build())
                            .guaranteeComponents(List.of(EGuarantee.HELP_DESK))
                            .build(),

                    ProductDto.builder()
                            .name("PRODUCT FOUR")
                            .categoryDto(CategoryDto.builder()
                                    .name("CATEGORY THREE")
                                    .build())
                            .id(4L)
                            .guaranteeComponents(List.of(
                                    EGuarantee.HELP_DESK, EGuarantee.EXCHANGE, EGuarantee.MONEY_BACK))
                            .build())

    );

    List<CustomerOrder> mockResult = List.of(

            CustomerOrder.builder()
                    .id(1L)
                    .product(Product.builder()
                            .name("PRODUCT THREE")
                            .category(Category.builder().name("CATEGORY THREE").build())
                            .id(3L)
                            .guaranteeComponents(List.of(
                                    EGuarantee.HELP_DESK
                            ))
                            .build()
                    )
                    .build(),

            CustomerOrder.builder()
                    .id(2L)
                    .product(Product.builder()
                            .name("PRODUCT FIVE")
                            .category(Category.builder().name("CATEGORY FIVE").build())
                            .id(5L)
                            .guaranteeComponents(List.of(
                                    EGuarantee.EXCHANGE
                            ))
                            .build()
                    )
                    .build(),

            CustomerOrder.builder()
                    .id(3L)
                    .product(Product.builder()
                            .name("PRODUCT ONE")
                            .category(Category.builder().name("CATEGORY ONE").build())
                            .id(1L)
                            .guaranteeComponents(List.of(
                                    EGuarantee.SERVICE))
                            .build())
                    .build(),

            CustomerOrder.builder()
                    .id(4L)
                    .product(Product.builder()
                            .id(2L)
                            .name("PRODUCT TWO")
                            .category(Category.builder().name("CATEGORY TWO").build())
                            .guaranteeComponents(List.of(
                                    EGuarantee.HELP_DESK))
                            .build())
                    .build(),

            CustomerOrder.builder()
                    .id(5L)
                    .product(Product.builder()
                            .id(4L)
                            .name("PRODUCT FOUR")
                            .category(Category.builder().name("CATEGORY THREE").build())
                            .guaranteeComponents(List.of(
                                    EGuarantee.HELP_DESK, EGuarantee.EXCHANGE, EGuarantee.MONEY_BACK))
                            .build())
                    .build()

    );


    given(customerOrderRepository.findProductsWithActiveWarranty())
            .willReturn(mockResult);

    //when
    //then
    assertDoesNotThrow(() -> {
      Map<String, List<ProductDto>> actualResult = customerOrderService.getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(guaranteeComponents);
      assertThat(actualResult, is(equalTo(expectedResult)));
    });
    then(customerOrderRepository).should(times(1)).findProductsWithActiveWarranty();
  }


  @Test
  @DisplayName("getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan. Not valid arguments. Proper exception should be thrown")
  void test11() {

    //given
    LocalDate minDate = null;
    LocalDate maxDate = null;
    BigDecimal minPriceAfterDiscount = new BigDecimal("300");
    String expectedExceptionMessage = String.format("At least one of the method arguments's not valid: minDate:%s maxDate: %s minPriceAfterDiscount: %s", minDate, maxDate, minPriceAfterDiscount);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> customerOrderService.getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(customerOrderRepository).should(never()).findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount);
  }

  @Test
  @DisplayName("getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan: minDate is after maxDate")
  void test12() {

    //given
    LocalDate minDate = LocalDate.of(2020, 10, 20);
    LocalDate maxDate = LocalDate.of(2019, 5, 10);

    BigDecimal minPriceAfterDiscount = new BigDecimal("300");
    String expectedExceptionMessage = String.format("minDate: " + minDate + " is after maxDate: " + maxDate);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> customerOrderService.getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(customerOrderRepository).should(never()).findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount);
  }

  @Test
  @DisplayName("getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan")
  void test13() {

    //given
    LocalDate minDate = LocalDate.of(2010, 5, 10);
    LocalDate maxDate = LocalDate.of(2017, 6, 22);
    BigDecimal minPriceAfterDiscount = new BigDecimal("1000");


    List<CustomerOrder> returnValue = List.of(
            CustomerOrder.builder()
                    .id(1L)
                    .quantity(10)
                    .date(LocalDate.of(2012, 6, 13))
                    .customer(Customer.builder()
                            .id(1L)
                            .name("JOHN")
                            .surname("STONE")
                            .age(30)
                            .country(Country.builder()
                                    .id(2L)
                                    .name("USA")
                                    .build())
                            .build())
                    .discount(new BigDecimal(0.1))
                    .payment(Payment.builder()
                            .id(1L)
                            .epayment(Epayment.CARD)
                            .build())
                    .product(Product.builder()
                            .name("PRODUCT ONE")
                            .id(1L)
                            .price(new BigDecimal("2000"))
                            .producer(Producer.builder()
                                    .id(1L)
                                    .country(Country.builder()
                                            .id(1L)
                                            .name("ENGLAND")
                                            .build())
                                    .trade(Trade.builder()
                                            .id(1L)
                                            .name("TRADE ONE")
                                            .build())
                                    .build())
                            .build())
                    .build(),

            CustomerOrder.builder()
                    .id(2L)
                    .quantity(35)
                    .date(LocalDate.of(2015, 2, 16))
                    .customer(Customer.builder()
                            .id(3L)
                            .name("ALICE")
                            .surname("DRINKWATER")
                            .age(55)
                            .country(Country.builder()
                                    .id(2L)
                                    .name("USA")
                                    .build())
                            .build())
                    .discount(new BigDecimal(0.3))
                    .payment(Payment.builder()
                            .id(2L)
                            .epayment(Epayment.MONEY_TRANSFER)
                            .build())
                    .product(Product.builder()
                            .name("PRODUCT TWO")
                            .id(2L)
                            .price(new BigDecimal("5000"))
                            .producer(Producer.builder()
                                    .id(2L)
                                    .country(Country.builder()
                                            .id(5L)
                                            .name("POLAND")
                                            .build())
                                    .trade(Trade.builder()
                                            .id(2L)
                                            .name("TRADE TWO")
                                            .build())
                                    .build())
                            .build())
                    .build()

    );
    given(customerOrderRepository
            .findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount))
            .willReturn(returnValue);
    //when
    //then
    assertDoesNotThrow(() -> {
      List<CustomerOrderDto> actualResult = customerOrderService.getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount);
      assertThat(actualResult, is(equalTo(returnValue.stream().map(ModelMapper::mapCustomerOrderToCustomerOrderDto).collect(Collectors.toList()))));
    });

    then(customerOrderRepository).should(times(1)).findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount);
  }
}
