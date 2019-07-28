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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

//  @Test
//  @DisplayName("getProductsOrderedByCustomerGroupedByProducer - proper argument should not throw an exception")
//  void test5(){
//
//    //given
//    given(customerOrderRepository.findProductsOrderedByCustomer(isNotNull(), isNotNull(), isNotNull()))
//            .willReturn(Map.ofEntries(
//                    Map.entry(ProducerDto.builder().id(1L).name("KOTLIN").trade(TradeDto.builder().id(1L).name("FOOD").build()).build(), );
//
//    //when
//    //then
//     Assertions.assertDoesNotThrow(() -> customerOrderService.getProductsOrderedByCustomerGroupedByProducer("JOHN", "SMITH", "ENGLAND"));
//
//  }

  @Test
  @DisplayName("getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder")
  void test5() {

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
  void test6() {

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
  void test7() {

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
  void test8() {

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
  void test9() {

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
//    assertDoesNotThrow(() -> {
    Map<String, List<ProductDto>> actualResult = customerOrderService.getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(guaranteeComponents);
    assertThat(actualResult, is(equalTo(expectedResult)));
//    });
    then(customerOrderRepository).should(times(1)).findProductsWithActiveWarranty();
  }

  @Test
  @DisplayName("getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan")
  void test10() {

    //given
    LocalDate minDate = LocalDate.of(2010, 5, 10);
    LocalDate maxDate = LocalDate.of(2017, 6, 22);
    String val;
    BigDecimal minPriceAfterDiscount = new BigDecimal("1000");

    ArgumentCaptor<LocalDate> minDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
    ArgumentCaptor<LocalDate> maxDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
    ArgumentCaptor<BigDecimal> minPriceAfterDiscountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
// TODO: 2019-07-28
    given(customerOrderRepository
            .findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDateCaptor.capture(), maxDateCaptor.capture(), minPriceAfterDiscountCaptor.capture()))
            .willReturn(List.of(


            ));
    //when
    //then
  }
}
