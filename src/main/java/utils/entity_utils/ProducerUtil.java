package utils.entity_utils;

import domain.Country;
import domain.Producer;
import domain.Trade;
import exception.AppException;
import validator.impl.ProducerValidator;

import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;

public class ProducerUtil {

  private ProducerUtil() {
  }

  public static Producer createProducerFromUserInput() {

    return Producer.builder()
            .name(getString("Input producer name"))
            .trade(Trade.builder()
                    .name(getString("Input trade name"))
                    .build())
            .country(Country.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  public static Producer getProducerIfValid(Producer producer) {

    var producerValidator = new ProducerValidator();

    var errorsMap = producerValidator.validate(producer);

    if (producerValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Producer is not valid: " + producerValidator.getErrors());
    }

    return producer;
  }

}
