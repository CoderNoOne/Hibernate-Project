package validator.impl;

import dto.PaymentDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@DisplayName("Test cases for paymentDtoValidator")
class PaymentDtoValidatorTest {

  private final PaymentDtoValidator paymentDtoValidator = new PaymentDtoValidator();

  @Test
  @DisplayName("Payment object is null")
  void test1() {

    //given
    PaymentDto paymentDto = null;
    Map<String, String> expectedErrors = Map.of(
            "Payment object", "Payment object is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = paymentDtoValidator.validate(paymentDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(paymentDtoValidator.hasErrors(), is(true));
            }
    );

  }

  @Test
  @DisplayName("Epayment property is null")
  void test2() {

    //given
    PaymentDto paymentDto = PaymentDto.builder()
            .epayment(null)
            .build();

    Map<String, String> expectedErrors = Map.of(
            "Epayment object", "Epayment is null");

    //when
    //then
    Assertions.assertDoesNotThrow(
            () -> {
              Map<String, String> actualErrors = paymentDtoValidator.validate(paymentDto);
              assertThat(actualErrors, is(equalTo(expectedErrors)));
              assertThat(paymentDtoValidator.hasErrors(), is(true));
            }
    );

  }
}
