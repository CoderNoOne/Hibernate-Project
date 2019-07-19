package validator.impl;

import dto.CountryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@Tag("Validators")
@DisplayName("Test cases for CountryDtoValidator")
class CountryDtoValidatorTest {

  private CountryDtoValidator countryDtoValidator = new CountryDtoValidator();

  @Test
  @DisplayName("CountryDto is null")
  void test1() {

    //given
    CountryDto countryDto = null;
    Map<String, String> expectedErrors = Map.of(
            "Country object", "Country object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = countryDtoValidator.validate(countryDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(countryDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CountryDto name is not valid")
  void test2() {

    //given
    CountryDto countryDto = CountryDto.builder()
            .name("country")
            .build();
    Map<String, String> expectedErrors = Map.of(
            "Country name", "Country name should contain only capital letters and optionally a single whitespace between them"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = countryDtoValidator.validate(countryDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(countryDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CountryDto is valid")
  void test3() {

    //given
    CountryDto countryDto = CountryDto.builder()
            .name("COUNTRY")
            .build();
    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = countryDtoValidator.validate(countryDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(countryDtoValidator.hasErrors(), is(false));
            }
    );
  }
}
