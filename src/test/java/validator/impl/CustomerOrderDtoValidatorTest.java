package validator.impl;


import domain.enums.EGuarantee;
import domain.enums.Epayment;
import dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@DisplayName("Test cases for customerOrderDtoValidator")
class CustomerOrderDtoValidatorTest {

  private final CustomerOrderDtoValidator customerOrderDtoValidator = new CustomerOrderDtoValidator();
  private final CustomerOrderDto validCustomerOrderDto = getValidCustomerOrderDto();

  @Test
  @DisplayName("CustomerOrderDto is null")
  void test1() {

    //given
    CustomerOrderDto customerOrderDto = null;
    Map<String, String> expectedErros = Map.of(
            "CustomerOrderDto object", "CutomerOrder object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErros)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("CustomerOrderDto discount is not valid")
  void test2() {

    //given
    CustomerOrderDto customerOrderDto1 = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(validCustomerOrderDto.getDate())
            .product(validCustomerOrderDto.getProduct())
            .payment(validCustomerOrderDto.getPayment())
            .discount(new BigDecimal("1.1"))
            .quantity(validCustomerOrderDto.getQuantity())
            .build();

    CustomerOrderDto customerOrderDto2 = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(validCustomerOrderDto.getDate())
            .product(validCustomerOrderDto.getProduct())
            .payment(validCustomerOrderDto.getPayment())
            .discount(new BigDecimal("-0.1"))
            .quantity(validCustomerOrderDto.getQuantity())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Discount", "Discount should be in the range <0.0,1.0>");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto1);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
              Map<String, String> actualErrors2 = customerOrderDtoValidator.validate(customerOrderDto2);
              assertThat(actualErrors2, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Order date is not valid")
  void test3() {

    //given
    CustomerOrderDto customerOrderDto = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(LocalDate.now().minusDays(1))
            .product(validCustomerOrderDto.getProduct())
            .payment(validCustomerOrderDto.getPayment())
            .discount(validCustomerOrderDto.getDiscount())
            .quantity(validCustomerOrderDto.getQuantity())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Order date", "Order date should be at present day or in the future");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Product Quantity is not valid")
  void test4() {
    //given
    CustomerOrderDto customerOrderDto = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(validCustomerOrderDto.getDate())
            .product(validCustomerOrderDto.getProduct())
            .payment(validCustomerOrderDto.getPayment())
            .discount(validCustomerOrderDto.getDiscount())
            .quantity(0)
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Product Quantity", "Product quantity should be greater than 0");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );
  }


  @Test
  @DisplayName("Customer is not valid")
  void test5() {

    //given
    CustomerOrderDto customerOrderDto = CustomerOrderDto.builder()
            .customer(CustomerDto.builder().name("name123").build())
            .date(validCustomerOrderDto.getDate())
            .product(validCustomerOrderDto.getProduct())
            .payment(validCustomerOrderDto.getPayment())
            .discount(validCustomerOrderDto.getDiscount())
            .quantity(validCustomerOrderDto.getQuantity())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Customer name", "Customer name should contains only capital letters and space between letters",
            "Customer surname", "Customer surname should contains only capital letters and space between letters",
            "Customer age", "Customer age should be greater than or equal to 18",
            "Country object", "Country object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Product is not valid")
  void test6() {

    //given
    CustomerOrderDto customerOrderDto = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(validCustomerOrderDto.getDate())
            .product(ProductDto.builder().name("product123").build())
            .payment(validCustomerOrderDto.getPayment())
            .discount(validCustomerOrderDto.getDiscount())
            .quantity(validCustomerOrderDto.getQuantity())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Product name", "Product name should contain only capital letters and optionally a whitespace between letters",
            "Product price", "Product price should be greater than 0",
            "Guarantee components", "Guarantee components should be unique",
            "Producer object", "Producer object is null",
            "Category object", "Category object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }


  @Test
  @DisplayName("Payment is not valid")
  void test7() {

    //given
    CustomerOrderDto customerOrderDto = CustomerOrderDto.builder()
            .customer(validCustomerOrderDto.getCustomer())
            .date(validCustomerOrderDto.getDate())
            .product(validCustomerOrderDto.getProduct())
            .payment(PaymentDto.builder().build())
            .discount(validCustomerOrderDto.getDiscount())
            .quantity(validCustomerOrderDto.getQuantity())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Epayment object", "Epayment is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(customerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("CustomerOrderDto is valid")
  void test8() {

    //given
    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerOrderDtoValidator.validate(validCustomerOrderDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerOrderDtoValidator.hasErrors(), is(false));
            }
    );
  }

  private CustomerOrderDto getValidCustomerOrderDto() {

    return CustomerOrderDto.builder()
            .quantity(20)
            .discount(new BigDecimal("0.2"))
            .payment(PaymentDto.builder()
                    .epayment(Epayment.CASH)
                    .build())
            .product(ProductDto.builder()
                    .name("PRODUCT")
                    .price(new BigDecimal("300"))
                    .guaranteeComponents(List.of(EGuarantee.SERVICE, EGuarantee.EXCHANGE))
                    .categoryDto(CategoryDto.builder()
                            .name("CATEGORY")
                            .build())
                    .producerDto(ProducerDto.builder()
                            .name("PRODUCER")
                            .trade(TradeDto.builder()
                                    .name("TRADE")
                                    .build())
                            .country(CountryDto.builder()
                                    .name("COUNTRY")
                                    .build())
                            .build())
                    .build())
            .customer(CustomerDto.builder()
                    .name("NAME")
                    .surname("SURNAME")
                    .age(18)
                    .countryDto(CountryDto.builder()
                            .name("COUNTRY")
                            .build())
                    .build())
            .date(LocalDate.now().plusMonths(2))
            .build();
  }
}
