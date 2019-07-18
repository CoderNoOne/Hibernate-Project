package util.entity_utils;

import dto.CategoryDto;
import exception.AppException;
import validator.impl.CategoryDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.getString;
import static util.others.UserDataUtils.printMessage;

public interface CategoryUtil {

  static CategoryDto createCategoryDtoFromUserInput() {

    return CategoryDto.builder()
            .name(getString("Input category name"))
            .build();
  }

  static CategoryDto getCategoryDtoIfValid(CategoryDto categoryDto) {

    var categoryDtoValidator = new CategoryDtoValidator();
    var errorsMap = categoryDtoValidator.validate(categoryDto);

    if (categoryDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException(String.format("CategoryDto is not valid: %s (errors: %s )", categoryDto, errorsMap));
    }
    return categoryDto;
  }

  static CategoryDto specifyCategoryDtoDetailToDelete() {

    printMessage("\nInput category's information you want to delete\n");

    return createCategoryDtoFromUserInput();
  }
}
