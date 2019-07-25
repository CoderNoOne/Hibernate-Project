package util.entity_utils;

import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import exception.AppException;
import validator.impl.ProducerDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public interface ProducerUtil {

  static ProducerDto getProducerDtoToUpdate(Long id) {

    return ProducerDto.builder()
            .id(id)
            .name(getString("Do you want to update producer name? (y/n)")
                    .equalsIgnoreCase("y") ? getString("Specify new producer name") : null)
            .country(getString("Do you want to update producer country name? (y/n)")
                    .equalsIgnoreCase("y") ? CountryDto.builder()
                    .name(getString("Specify new country name")).build() : null)
            .build();

  }

  static ProducerDto createProducerDtoFromUserInput() {

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

  static ProducerDto getProducerDtoIfValid(ProducerDto producerDto) {

    var producerDtoValidator = new ProducerDtoValidator();

    var errorsMap = producerDtoValidator.validate(producerDto);

    if (producerDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Producer is not valid: " + producerDtoValidator.getErrors());
    }

    return producerDto;
  }

}
