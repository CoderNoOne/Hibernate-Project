package validator.impl;

import domain.enums.EGuarantee;
import dto.*;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Validators")
@DisplayName("Test cases for ProductDtoValidator")
class ProductDtoValidatorTest {

  private final ProductDtoValidator productDtoValidator = new ProductDtoValidator();
  private final ProductDto validProductDto = getValidProductDto();

  @Test
  @DisplayName("ProductDto is null")
  void test1() {

    //given
    ProductDto productDto = null;
    Map<String, String> expectedErrors = Map.of("Product object", "Product object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(productDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Product name is not valid")
  void test2() {

    //given
    ProductDto productDto = ProductDto.builder()
            .name("product123")
            .categoryDto(validProductDto.getCategoryDto())
            .guaranteeComponents(validProductDto.getGuaranteeComponents())
            .price(validProductDto.getPrice())
            .producerDto(validProductDto.getProducerDto())

            .build();
    Map<String, String> expectedErrors = Map.of(
            "Product name", "Product name should contain only capital letters and optionally a whitespace between letters");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(productDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(true));
            }
    );
  }


  @Test
  @DisplayName("Product price is not valid")
  void test3() {

    //given
    ProductDto productDto = ProductDto.builder()
            .name(validProductDto.getName())
            .categoryDto(validProductDto.getCategoryDto())
            .guaranteeComponents(validProductDto.getGuaranteeComponents())
            .price(new BigDecimal("0"))
            .producerDto(validProductDto.getProducerDto())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Product price", "Product price should be greater than 0");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(productDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Product components are not valid")
  void test4() {
    //given
    ProductDto productDto = ProductDto.builder()
            .name(validProductDto.getName())
            .categoryDto(validProductDto.getCategoryDto())
            .guaranteeComponents(List.of(EGuarantee.SERVICE, EGuarantee.SERVICE))
            .price(validProductDto.getPrice())
            .producerDto(validProductDto.getProducerDto())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Guarantee components", "Guarantee components should be unique");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(productDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("Producer is not valid")
  void test5() {

    //given
    ProductDto productDto = ProductDto.builder()
            .name(validProductDto.getName())
            .categoryDto(validProductDto.getCategoryDto())
            .guaranteeComponents(validProductDto.getGuaranteeComponents())
            .price(validProductDto.getPrice())
            .producerDto(ProducerDto.builder().build())
            .build();


    Map<String, String> expectedErrors = Map.of(
            "Producer name", "Producer name should contain only capital letters and optionally a whitespace between them",
            "Trade object", "Trade object is null",
            "Country object", "Country object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(productDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ProductDto is valid")
  void test6() {

    //given
    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = productDtoValidator.validate(validProductDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(productDtoValidator.hasErrors(), is(false));
            }
    );

  }

  private ProductDto getValidProductDto() {
    return ProductDto.builder()
            .name("PRODUCT")
            .categoryDto(CategoryDto.builder()
                    .name("CATEGORY")
                    .build())
            .guaranteeComponents(List.of(EGuarantee.SERVICE))
            .price(new BigDecimal("3000"))
            .producerDto(ProducerDto.builder()
                    .name("PRODUCER")
                    .country(CountryDto.builder()
                            .name("COUNTRY")
                            .build())
                    .trade(TradeDto.builder()
                            .name("TRADE")
                            .build())
                    .build())
            .build();
  }
}
