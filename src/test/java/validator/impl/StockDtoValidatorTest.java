package validator.impl;

import domain.enums.EGuarantee;
import dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("Validators")
@DisplayName("Test cases for stockDtoValidator")
class StockDtoValidatorTest {

  private final StockDtoValidator stockDtoValidator = new StockDtoValidator();
  private final StockDto validStockDto;

  {
    validStockDto = StockDto.builder()
            .quantity(10)
            .shopDto(ShopDto.builder()
                    .name("SHOP")
                    .countryDto(CountryDto.builder()
                            .name("COUNTRY")
                            .build())
                    .build())
            .productDto(ProductDto.builder()
                    .name("PRODUCT")
                    .price(new BigDecimal("300"))
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
                    .guaranteeComponents(List.of(EGuarantee.SERVICE, EGuarantee.EXCHANGE))
                    .build())
            .build();
  }

  @Test
  @DisplayName("Stock is null")
  void test1() {

    //given
    StockDto stockDto = null;
    Map<String, String> expectedErrors = Map.of
            ("Stock object", "Stock object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = stockDtoValidator.validate(stockDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(stockDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("Quantity is not valid")
  void test2() {

    //given
    StockDto stockDto = StockDto.builder()
            .quantity(-100)
            .productDto(validStockDto.getProductDto())
            .shopDto(validStockDto.getShopDto())
            .build();

    Map<String, String> expectedErrors = Map.of
            ("Quantity", "quantity should be greater or equal to 0");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = stockDtoValidator.validate(stockDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(stockDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ProductDto is not valid")
  void test3() {

    //given
    StockDto stockDto = StockDto.builder()
            .quantity(validStockDto.getQuantity())
            .productDto(ProductDto.builder().build())
            .shopDto(validStockDto.getShopDto())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Product name", "Product name should contain only capital letters and optionally a whitespace between letters",
            "Product price", "Product price should be greater than 0",
            "Producer object", "Producer object is null",
            "Category object", "Category object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = stockDtoValidator.validate(stockDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(stockDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ShopDto is not valid")
  void test4() {

    //given
    StockDto stockDto = StockDto.builder()
            .quantity(validStockDto.getQuantity())
            .productDto(validStockDto.getProductDto())
            .shopDto(ShopDto.builder().build())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Shop name", " Shop name should contain only capital letters and optionally white space between letters");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = stockDtoValidator.validate(stockDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(stockDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("StockDto is valid")
  void test5() {

    //given
    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = stockDtoValidator.validate(validStockDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(stockDtoValidator.hasErrors(), is(false));
            }
    );

  }
}
