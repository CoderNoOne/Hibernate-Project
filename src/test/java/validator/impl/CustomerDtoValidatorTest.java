package validator.impl;

import dto.CountryDto;
import dto.CustomerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Validators")
@DisplayName("Test cases for customerDto")
class CustomerDtoValidatorTest {

  private CustomerDtoValidator customerDtoValidator = new CustomerDtoValidator();

  @Test
  @DisplayName("CustomerDto is null")
  void test1() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Customer object", "customer object is null");
    CustomerDto customerDto = null;

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("CustomerDto name is not valid")
  void test2() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Customer name", "Customer name should contains only capital letters and space between letters"
    );
    CustomerDto customerDto = CustomerDto.builder()
            .name("name")
            .surname("SURNAME")
            .age(24)
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then

    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CustomerDto surname is not valid")
  void test3() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Customer surname", "Customer surname should contains only capital letters and space between letters"
    );
    CustomerDto customerDto = CustomerDto.builder()
            .name("NAME")
            .surname("surname")
            .age(24)
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CustomerDto age is not valid")
  void test4() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Customer age", "Customer age should be greater than or equal to 18"
    );

    CustomerDto customerDto = CustomerDto.builder()
            .name("NAME")
            .surname("SURNAME")
            .age(17)
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CustomerDto country name is not valid")
  void test5() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Country name", "Country name should contain only capital letters and optionally a single whitespace between them"
    );

    CustomerDto customerDto = CustomerDto.builder()
            .name("NAME")
            .surname("SURNAME")
            .age(18)
            .countryDto(CountryDto.builder()
                    .name("country")
                    .build())
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );
  }

  @Test
  @DisplayName("CustomerDto country is null")
  void test6() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Country object", "Country object is null"
    );

    CustomerDto customerDto = CustomerDto.builder()
            .name("NAME")
            .surname("SURNAME")
            .age(18)
            .countryDto(null)
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("CustomerDto is valid")
  void test7() {

    //given
    Map<String, String> expectedErrors = Collections.emptyMap();

    CustomerDto customerDto = CustomerDto.builder()
            .name("NAME")
            .surname("SURNAME")
            .age(18)
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(false));
            }
    );
  }

  @Test
  @DisplayName("All customerDto fields are not valid")
  void test8() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Customer age", "Customer age should be greater than or equal to 18",
            "Customer name", "Customer name should contains only capital letters and space between letters",
            "Country name", "Country name should contain only capital letters and optionally a single whitespace between them",
            "Customer surname", "Customer surname should contains only capital letters and space between letters"
    );

    CustomerDto customerDto = CustomerDto.builder()
            .name("name")
            .surname("surname")
            .age(17)
            .countryDto(CountryDto.builder()
                    .name("country")
                    .build())
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = customerDtoValidator.validate(customerDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(customerDtoValidator.hasErrors(), is(true));
            }
    );
  }
}
