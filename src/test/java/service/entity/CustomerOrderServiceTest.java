package service.entity;

import domain.*;
import domain.enums.Epayment;
import dto.CountryDto;
import dto.CustomerDto;
import dto.CustomerOrderDto;
import dto.ProductDto;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
                    .price(new BigDecimal("300"))
                    .name("PRODUCT ONE")
                    .build(),

            Product.builder()
                    .id(2L)
                    .price(new BigDecimal("20"))
                    .name("PRODUCT TWO")
                    .build()
    );
    given(customerOrderRepository.findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(countryNameCaptor.capture(), minAgeCaptor.capture(), maxAgeCaptor.capture()))
            .willReturn(repositoryMethodResultList);

    //when
    //then
    assertDoesNotThrow(() -> {
      List<ProductDto> actualProductDtoList = customerOrderService.getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder(countryName, minAge, maxAge);
      assertThat(actualProductDtoList,is(equalTo(repositoryMethodResultList.stream().map(ModelMapper::mapProductToProductDto).collect(Collectors.toList()))));
    });

    then(customerOrderRepository).should(times(1)).findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange("USA", 20, 300);
  }

  
}
