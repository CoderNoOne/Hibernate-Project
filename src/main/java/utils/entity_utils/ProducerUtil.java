package utils.entity_utils;

import domain.Country;
import domain.Producer;
import domain.Trade;
import exception.AppException;
import validator.impl.ProducerValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.*;

public class ProducerUtil {

  private static final ProducerValidator producerValidator = new ProducerValidator();

  private ProducerUtil() {
  }

  public static Producer createProducerFromUserInput() {

    var name = getString("Input producer name");
    var trade = Trade.builder().name(getString("Input trade name")).build();
    var country = Country.builder().name(getString("Input country name")).build();

    var producer = Producer.builder()
            .trade(trade)
            .name(name)
            .country(country)
            .build();

    var errorsMap = producerValidator.validate(producer);

    if (producerValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Producer is not valid: " + producerValidator.getErrors());
    }

    return producer;
  }
}
