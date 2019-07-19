package validator.impl;

import dto.ShopDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("Validators")
@DisplayName("Test cases for ShopDtoValidar")
class ShopDtoValidatorTest {

  private final ShopDtoValidator shopDtoValidator = new ShopDtoValidator();

  @Test
  @DisplayName("ShopDto is null")
  void test1() {

    //given
    ShopDto shopDto = null;
    Map<String, String> expectedErrors = Map.of("Shop object", "Shop object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = shopDtoValidator.validate(shopDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(shopDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ShopDto name is not valid")
  void test2() {

    //given
    ShopDto shopDto = ShopDto.builder().name("shop123").build();
    Map<String, String> expectedErrors = Map.of("Shop name", " Shop name should contain only capital letters and optionally white space between letters");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = shopDtoValidator.validate(shopDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(shopDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ShopDto name is null")
  void test3() {

    //given
    ShopDto shopDto = ShopDto.builder().build();
    Map<String, String> expectedErrors = Map.of("Shop name", " Shop name should contain only capital letters and optionally white space between letters");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = shopDtoValidator.validate(shopDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(shopDtoValidator.hasErrors(), is(true));
            }
    );


  }
}
