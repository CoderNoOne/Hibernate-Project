package validator.impl;

import dto.CategoryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("Test cases for categoryDtoValidator")
class CategoryDtoValidatorTest {


  private CategoryDtoValidator categoryDtoValidator = new CategoryDtoValidator();

  @Test
  @DisplayName("CategoryDto is null")
  void test1() {

    //given
    CategoryDto categoryDto = null;
    Map<String, String> expectedErrors = Map.of(
            "Category object", "Category object is null"
    );

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      Map<String, String> actualErrors = categoryDtoValidator.validate(categoryDto);
      assertThat(actualErrors, is(equalTo(expectedErrors)));
      assertThat(categoryDtoValidator.hasErrors(), is(true));
    });
  }

  @Test
  @DisplayName("CategoryDto name is not valid")
  void test2() {

    //given
    Map<String, String> expectedErrors = Map.of(
            "Category name", "Category name is not valid"
    );
    CategoryDto categoryDto = CategoryDto.builder()
            .name("category1")
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      Map<String, String> actualErrors = categoryDtoValidator.validate(categoryDto);
      assertThat(actualErrors, is(equalTo(expectedErrors)));
      assertThat(categoryDtoValidator.hasErrors(), is(true));
    });

  }

  @Test
  @DisplayName("CategoryDto is valid")
  void test3() {

    //given
    CategoryDto categoryDto = CategoryDto.builder()
            .name("CATEGORY")
            .build();

    Map<String, String> expectedErrors = Collections.emptyMap();

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      Map<String, String> actualErrors = categoryDtoValidator.validate(categoryDto);
      assertThat(actualErrors, is(equalTo(expectedErrors)));
      assertThat(categoryDtoValidator.hasErrors(), is(false));
    });
  }

}
