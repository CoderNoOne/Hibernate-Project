package util.entity_utils;

import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import exception.AppException;
import validator.impl.ProducerDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public class ProducerUtil {

  private ProducerUtil() {
  }

  public static ProducerDto createProducerDtoFromUserInput() {

    return ProducerDto.builder()
            .name(getString("Input producer name"))
            .trade(TradeDto.builder()
                    .name(getString("Input trade name"))
                    .build())
            .country(CountryDto.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  public static ProducerDto getProducerDtoIfValid(ProducerDto producerDto) {

    var producerDtoValidator = new ProducerDtoValidator();

    var errorsMap = producerDtoValidator.validate(producerDto);

    if (producerDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Producer is not valid: " + producerDtoValidator.getErrors());
    }

    return producerDto;
  }

}
