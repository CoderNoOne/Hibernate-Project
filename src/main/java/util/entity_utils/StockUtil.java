package util.entity_utils;

import dto.CategoryDto;
import dto.ProductDto;
import dto.ShopDto;
import dto.StockDto;
import exception.AppException;
import validator.impl.StockDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getString;
import static util.others.UserDataUtils.printMessage;

public interface StockUtil {

  static StockDto getStockDtoToUpdate(Long id) {

    return StockDto.builder()
            .id(id)
            .quantity(getString("Do you want to update quantity? (y/n)")
                    .equalsIgnoreCase("y") ? getInt("Specify new quantity") : null)
            .build();

  }

  static StockDto createStockDtoDetailFromUserInput() {

    return StockDto.builder()
            .productDto(ProductDto.builder()
                    .name(getString("Input product name"))
                    .categoryDto(CategoryDto.builder()
                            .name(getString("Input category name"))
                            .build())
                    .build())
            .shopDto(ShopDto.builder()
                    .name(getString("Input shop name"))
                    .build())
            .quantity(getInt("Input stock quantity"))
            .build();
  }

  static StockDto getStockDtoIfValid(StockDto stockDto) {

    if (stockDto == null) {
      throw new AppException("Stock is null");
    }
    var stockDtoValidator = new StockDtoValidator();
    var errorsMap = stockDtoValidator.validate(stockDto);

    if (stockDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Stock is not valid");
    }
    return stockDto;
  }
}
