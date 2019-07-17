package validator.impl;

import dto.CountryDto;
import dto.CustomerDto;
import dto.TradeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Test cases for TradeDtoValidator")
class TradeDtoValidatorTest {

  private final TradeDtoValidator tradeDtoValidator = new TradeDtoValidator();

  @Test
  @DisplayName("Trade is null")
  void test1() {
    //given
    Map<String, String> expectedErrors = Map.of(
            "Trade object", "Trade object is null"
    );

    TradeDto tradeDto = null;

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = tradeDtoValidator.validate(tradeDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(tradeDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("Trade name is not valid")
  void test2(){

    //given
    Map<String, String> expectedErrors = Map.of(
            "Trade name", "Trade name should contain only capital letters and optionally whitespace between letters"
    );

    TradeDto tradeDto = TradeDto.builder().name("trade123").build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = tradeDtoValidator.validate(tradeDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(tradeDtoValidator.hasErrors(), is(true));
            }
    );
  }
}
