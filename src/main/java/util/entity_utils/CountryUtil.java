package util.entity_utils;

import dto.CountryDto;
import exception.AppException;
import validator.impl.CountryDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.getString;
import static util.others.UserDataUtils.printMessage;

public interface CountryUtil {

  static CountryDto createCountryDtoFromUserInput() {

    return CountryDto.builder()
            .name(getString("Input country name"))
            .build();
  }

  static CountryDto getCountryDtoIfValid(CountryDto countryDto) {

    var countryDtoValidator = new CountryDtoValidator();
    var errorsMap = countryDtoValidator.validate(countryDto);

    if (countryDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("CountryDto is not valid: " + countryDto.getName());
    }
    return countryDto;
  }

  static CountryDto specifyCountryDtoDetailToDelete() {

    printMessage("\nInput country's information you want to delete\n");

    return createCountryDtoFromUserInput();
  }
}
