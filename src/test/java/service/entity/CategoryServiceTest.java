package service.entity;

import domain.*;
import domain.enums.EGuarantee;
import dto.CategoryDto;
import exception.AppException;
import mapper.ModelMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CategoryRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Tag("Services")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@DisplayName("Test cases for CategoryService")
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryService categoryService;

  private final Category category;

  {
    category = Category.builder()
            .id(1L)
            .name("COMPUTERS")
            .products(List.of(
                    Product.builder()
                            .id(1L)
                            .name("NOTEBOOK")
                            .category(Category.builder()
                                    .id(1L)
                                    .name("COMPUTERS")
                                    .build())
                            .producer(Producer.builder()
                                    .id(1L)
                                    .name("LENOVO")
                                    .trade(Trade.builder()
                                            .id(1L)
                                            .name("ELECTRONICS")
                                            .build())
                                    .country(Country.builder()
                                            .id(1L)
                                            .name("CHINA")
                                            .build())
                                    .build())
                            .guaranteeComponents(List.of(EGuarantee.SERVICE, EGuarantee.HELP_DESK))
                            .price(new BigDecimal("5000"))
                            .build()))
            .build();
  }


  @Test
  @DisplayName("get Category by name ")
  void test1() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS"))
            .willReturn(Optional.of(category));

    //when
    Optional<CategoryDto> categoryDtoByName = categoryService
            .getCategoryByName(CategoryDto.builder().name("COMPUTERS").build().getName());

    //then
    assertThat(categoryDtoByName.isPresent(), is(true));
    assertThat(categoryDtoByName, equalTo(Optional.of(
            CategoryDto.builder().id(1L).name("COMPUTERS").build())));
    then(categoryRepository).should(times(1)).findCategoryByName("COMPUTERS");
  }

  @Test
  @DisplayName("add correct category to db with proper values")
  void test2() {

    //given
    CategoryDto categoryDto = CategoryDto.builder().id(1L).name("COMPUTERS").build();

    given(categoryRepository
            .add(category))
            .willReturn(Optional.of(category));

    //when
    Optional<CategoryDto> categoryDtoResult = categoryService.addCategoryToDb(categoryDto);

    //then
    assertThat(categoryDtoResult, equalTo(Optional.of(CategoryDto.builder().id(1L).name("COMPUTERS").build())));
    then(categoryRepository).should(times(1)).add(ModelMapper.mapCategoryDtoToCategory(categoryDto));
  }

  @Test
  @DisplayName("add category to DB with categoryDto object null - should throw an AppException with an appropriate message")
  void test3() {

    //given
    CategoryDto categoryDto = null;

    //when
    //then
    AppException appException
            = assertThrows(AppException.class, () -> categoryService.addCategoryToDb(categoryDto));

    assertThat(appException.getMessage(), equalTo("Category is null"));
    then(categoryRepository).should(never()).add(any());
  }

  @Test
  @DisplayName("add category to DB with not unique Category - should throw an AppException")
  void test4() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS")).willReturn(Optional.of(category));

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> categoryService.addCategoryToDb(CategoryDto.builder().id(1L).name("COMPUTERS").build()));
    assertThat(appException.getMessage(), equalTo("Category is not unique by name: " + "COMPUTERS"));
    then(categoryRepository).should(times(1)).findCategoryByName("COMPUTERS");
  }


  @Test
  @DisplayName("get category from DB if category isn't in DB yet")
  void test5() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS")).willReturn(Optional.of(category));

    //when
    CategoryDto categoryDto = categoryService.getCategoryFromDbIfExists(CategoryDto.builder().name("COMPUTERS").build());

    //then
    assertThat(categoryDto, is(equalTo(CategoryDto.builder().id(1L).name("COMPUTERS").build())));
    then(categoryRepository).should(times(1)).findCategoryByName("COMPUTERS");
  }

  @Test
  @DisplayName("get category from DB if it exists in DB already")
  void test6() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS")).willReturn(Optional.empty());

    //when
    CategoryDto categoryDto = categoryService.getCategoryFromDbIfExists(CategoryDto.builder().name("COMPUTERS").build());

    //then
    assertThat(categoryDto, is(equalTo(CategoryDto.builder().name("COMPUTERS").build())));
    then(categoryRepository).should(times(1)).findCategoryByName("COMPUTERS");
  }

  @Test
  @DisplayName("get category from DB - categoryDto object is null")
  void test10() {

    //given
    CategoryDto categoryDto = null;
    String expectedExceptionMessage = "CategoryDto is null";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> categoryService.getCategoryFromDbIfExists(categoryDto));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));

  }

  @Test
  @DisplayName("find all categories")
  void test7() {

    //given
    given(categoryRepository.findAll())
            .willReturn(List
                    .of(category));

    //when
    List<CategoryDto> allCategories = categoryService.findAllCategories();

    //then
    assertThat(allCategories, hasSize(1));
    assertThat(allCategories.get(0).getName(), equalTo("COMPUTERS"));
    assertThat(allCategories.get(0).getId(), is(1L));
    then(categoryRepository).should(times(1)).findAll();

  }

  @ParameterizedTest
  @MethodSource("getNotValidCategoryName")
  @DisplayName("delete category by name with null or empty name should throw an exception with appropriate message")
  void test8(String name) {

    //given
    String expectedExceptionMessage = String.format("Category name is null/ undefined: %s", name);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> categoryService.deleteCategoryByName(name));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(categoryRepository).should(never()).deleteById(anyLong());
  }

  @TestFactory
  @DisplayName("delete category by name with valid name should not throw an exception")
  Collection<DynamicTest> test9() {

    return new Random().ints(10, 1, 11).boxed()
            .map(num -> {
              String generatedName = getRandomStringWithUpperCaseLetters(num);
              return DynamicTest.dynamicTest(generatedName, () -> assertDoesNotThrow(() -> categoryService.deleteCategoryByName(generatedName)));
            })
            .collect(Collectors.toList());
  }

  private static List<String> getNotValidCategoryName() {
    return Arrays.asList(
            null,
            ""
    );
  }

  private String getRandomStringWithUpperCaseLetters(int letterNumber) {

    String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    return IntStream.rangeClosed(1, letterNumber)
            .mapToObj(num -> letters.charAt((int) (Math.random() * letters.length())))
            .map(String::valueOf)
            .collect(Collectors.joining());
  }
}
