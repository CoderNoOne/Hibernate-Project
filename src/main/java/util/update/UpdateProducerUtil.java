package util.update;

import dto.CountryDto;
import dto.ProducerDto;
import util.update.enums.CustomerField;
import util.update.enums.ProducerField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getString;

public interface UpdateProducerUtil {

  static ProducerDto getUpdatedProducerDto(ProducerDto producerDto) {

    List<ProducerField> producerFields = Arrays.stream(ProducerField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(producerFields);
      CustomerField producerProperty = CustomerField.valueOf(getString("Choose what producer property you want to update. Not case sensitive").toUpperCase());

      switch (producerProperty) {
        case NAME -> {
          String updatedName = getString("Type producer new name");
          producerFields.remove(ProducerField.NAME);
          producerDto.setName(updatedName);
        }

        case COUNTRY -> {
          String updatedCountryName = getString("Type producer new country");
          producerFields.remove(ProducerField.COUNTRY);
          producerDto.setCountry(CountryDto.builder().name(updatedCountryName).build());
        }
        default -> printMessage("Not valid producer property");
      }
      hasNext = getString("Do you want to update another producer property? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !producerFields.isEmpty());

    return producerDto;
  }
}
