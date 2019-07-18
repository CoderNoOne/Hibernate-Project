package service.entity;

import domain.*;
import domain.enums.EGuarantee;
import dto.CategoryDto;
import exception.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CategoryRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
  }

  @Test
  @DisplayName("add correct category to db with proper values")
  void test2() {

    //given
    CategoryDto categoryDto = CategoryDto.builder().id(1L).name("COMPUTERS").build();

    given(categoryRepository
            .addOrUpdate(category))
            .willReturn(Optional.of(category));

    //when
    Optional<CategoryDto> categoryDtoResult = categoryService.addCategoryToDb(categoryDto);

    //then
    assertThat(categoryDtoResult, equalTo(Optional.of(CategoryDto.builder().id(1L).name("COMPUTERS").build())));
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
  }


  @Test
  @DisplayName("get category from DB if category isn't in DB yet")
  void test5() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS")).willReturn(Optional.of(category));

    //when
    CategoryDto categoryDto = categoryService.getCategoryFromDbIfExists(CategoryDto.builder().name("COMPUTERS").build());

    //then
    assertEquals(categoryDto, CategoryDto.builder().id(1L).name("COMPUTERS").build());
  }

  @Test
  @DisplayName("get category from DB if it exists in DB already")
  void test6() {

    //given
    given(categoryRepository.findCategoryByName("COMPUTERS")).willReturn(Optional.empty());

    //when
    CategoryDto categoryDto = categoryService.getCategoryFromDbIfExists(CategoryDto.builder().name("COMPUTERS").build());

    //then
    assertEquals(categoryDto, CategoryDto.builder().name("COMPUTERS").build());

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

  }

}
