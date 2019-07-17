package validator.impl;

import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test cases for producerDto")
class ProducerDtoValidatorTest {


  private final ProducerDtoValidator producerDtoValidator = new ProducerDtoValidator();
  private final ProducerDto validProducerDto = getValidProducerDto();

  @Test
  @DisplayName("Producer object is null")
  void test1() {

    //given
    ProducerDto producerDto = null;
    Map<String, String> expectedErrors = Map.of(
            "Producer object", "Producer object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("Producer name is not valid")
  void test2() {

    //given
    ProducerDto producerDto = ProducerDto.builder()
            .name("producer123")
            .country(validProducerDto.getCountry())
            .trade(validProducerDto.getTrade())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Producer name",
            "Producer name should contain only capital letters and optionally a whitespace between them"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("TradeDto is not valid")
  void test3() {

    //given
    ProducerDto producerDto = ProducerDto.builder()
            .name(validProducerDto.getName())
            .country(validProducerDto.getCountry())
            .trade(TradeDto.builder().build())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Trade name", "Trade name should contain only capital letters and optionally whitespace between letters"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("TradeDto is null")
  void test6() {

    //given
    ProducerDto producerDto = ProducerDto.builder()
            .name(validProducerDto.getName())
            .country(validProducerDto.getCountry())
            .trade(null)
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Trade object", "Trade object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CountryDto is null")
  void test7() {

    //given
    ProducerDto producerDto = ProducerDto.builder()
            .name(validProducerDto.getName())
            .country(null)
            .trade(validProducerDto.getTrade())
            .build();

    Map<String, String> expectedErrors = Map.of("Country object", "Country object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CountryDto is not valid")
  void test4() {

    //given
    ProducerDto producerDto = ProducerDto.builder()
            .name(validProducerDto.getName())
            .country(CountryDto.builder().build())
            .trade(validProducerDto.getTrade())
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Country name", "Country name should contain only capital letters and optionally a single whitespace between them"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(producerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("ProducerDto is valid")
  void test5() {
    //given

    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = producerDtoValidator.validate(validProducerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(producerDtoValidator.hasErrors(), is(false));
            }
    );
  }

  private ProducerDto getValidProducerDto() {

    return ProducerDto.builder()
            .name("PRODUCER")
            .trade(TradeDto.builder()
                    .name("TRADE")
                    .build())
            .country(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

  }
}
