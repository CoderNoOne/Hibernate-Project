package service.entity;

import domain.Country;
import domain.Shop;
import dto.CountryDto;
import dto.ShopDto;
import exception.AppException;
import mapper.ModelMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ShopRepository;
import util.update.enums.ShopField;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@Tag("Services")
@DisplayName("Test cases for ShopService")
class ShopServiceTest {

  @Mock
  private ShopRepository shopRepository;

  @Mock
  private CountryRepository countryRepository;

  @InjectMocks
  private ShopService shopService;

  @Test
  @DisplayName("addShop to Db with null argument should throw an exception with appropriate message")
  void test1() {

    //given
    ShopDto shopDto = null;
    String expectedExceptionMessage = "ShopDto object is null";

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> shopService.addShopToDbFromUserInput(shopDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
  }

  @Test
  @DisplayName("addShop to DB with empty String shopDto name should throw an exception")
  void test2() {

    //given
    ShopDto shopDto = ShopDto.builder()
            .name("")
            .build();

    String expectedExceptionMessage = String.format("Shop name is undefined/null: %s", shopDto.getName());

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> shopService.addShopToDbFromUserInput(shopDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
  }

  @Test
  @DisplayName("addShop to DB with not unique shopDto should throw an exception")
  void test3() {

    //given
    ShopDto shopDto = ShopDto.builder()
            .name("TRADE")
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    String expectedExceptionMessage = String.format("You couldn't add shop to db. Shop is not unique by name: %s and country: %s", shopDto.getName(), shopDto.getCountryDto().getName());

    given(shopRepository.findShopByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName())).willReturn(Optional.of(
            Shop.builder()
                    .id(1L)
                    .name(shopDto.getName())
                    .country(Country.builder()
                            .name(shopDto.getCountryDto().getName())
                            .build())
                    .build()));
    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> shopService.addShopToDbFromUserInput(shopDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));

  }

  @Test
  @DisplayName("add unique shop with appropriate name should not throw an exception")
  void test4() {

    //given
    ShopDto shopDto = ShopDto.builder()
            .name("SHOP")
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    given(shopRepository.findShopByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName())).willReturn(Optional.empty());

    given(countryRepository.findCountryByName(shopDto.getCountryDto().getName())).willReturn(Optional.empty());

    //when
    //then
    assertDoesNotThrow(() -> shopService.addShopToDbFromUserInput(shopDto));
  }

  @Test
  @DisplayName("get Shop from db if exists with null argument should throw an appropriate exception")
  void test5() {

    //given
    ShopDto shopDto = null;
    String expectedExceptionMessage = "ShopDto object is null";

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> shopService.getShopFromDbIfExists(shopDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(shopRepository).should(never()).findShopByNameAndCountry(anyString(), anyString());
  }


  @Test
  @DisplayName("getShop from db with valid argument should return appropriate value")
  void test6() {

    //given
    ShopDto shopDto = ShopDto.builder()
            .name("SHOP")
            .countryDto(CountryDto.builder()
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then
    assertDoesNotThrow(() -> {
      ShopDto actualResult = shopService.getShopFromDbIfExists(shopDto);
      assertThat(actualResult, not(nullValue()));
      assertThat(actualResult, is(equalTo(shopDto)));
    });
    then(shopRepository).should(times(1)).findShopByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName());
  }


  @Test
  @DisplayName("updateProduct shop name and country name")
  void test7() {

    //given
    ShopDto shopDtoToUpdate = ShopDto.builder()
            .id(1L)
            .name("MINIMAL")
            .countryDto(CountryDto.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();

    Map<ShopField, String> shopNewPropertyValues = Map.of(
            ShopField.NAME, "BIEDRONKA",
            ShopField.COUNTRY, "POLAND");


    Shop updatedShop = Shop.builder()
            .id(1L)
            .name("BIEDRONKA")
            .country(Country.builder()
                    .id(1L)
                    .name("POLAND")
                    .build())
            .build();


    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.addOrUpdate(argumentCaptor.capture()))
            .willReturn(Optional.of(updatedShop));

    given(countryRepository.findCountryByName("POLAND")).willReturn(Optional.of(Country.builder()
            .id(1L)
            .name("POLAND")
            .build()));


    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<ShopDto> actualUpdatedShop = shopService.update(shopDtoToUpdate, shopNewPropertyValues);
      assertAll(
              () -> assertThat(actualUpdatedShop, is(equalTo(Optional.of(updatedShop).map(ModelMapper::mapShopToShopDto)))),
              () -> assertThat(argumentCaptor.getValue(), is(equalTo(updatedShop))),
              () -> assertThat(actualUpdatedShop.isPresent(), is(true)),
              () -> assertThat(actualUpdatedShop.get().getName(), is(equalTo("BIEDRONKA"))),
              () -> assertThat(actualUpdatedShop.get().getCountryDto().getName(), is(equalTo("POLAND")))

      );
    }, "Shop properties (name and country name) should be properly updated");

    then(countryRepository).should(times(1)).findCountryByName(argumentCaptor.getValue().getCountry().getName());
    then(shopRepository).should(times(1)).addOrUpdate(updatedShop);
  }

  @Test
  @DisplayName("updateProduct shop name")
  void test8() {

    //given
    ShopDto shopDtoToUpdate = ShopDto.builder()
            .id(1L)
            .name("MINIMAL")
            .countryDto(CountryDto.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();

    Map<ShopField, String> shopNewPropertyValues = Map.of(
            ShopField.NAME, "BIEDRONKA"
    );


    Shop updatedShop = Shop.builder()
            .id(1L)
            .name("BIEDRONKA")
            .country(Country.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();


    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.addOrUpdate(argumentCaptor.capture()))
            .willReturn(Optional.of(updatedShop));

    given(countryRepository.findCountryByName("GERMANY")).willReturn(Optional.empty());

    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<ShopDto> actualUpdatedShop = shopService.update(shopDtoToUpdate, shopNewPropertyValues);
      assertAll(
              () -> assertThat(actualUpdatedShop, is(equalTo(Optional.of(updatedShop).map(ModelMapper::mapShopToShopDto)))),
              () -> assertThat(argumentCaptor.getValue(), is(equalTo(updatedShop))),
              () -> assertThat(actualUpdatedShop.isPresent(), is(true)),
              () -> assertThat(actualUpdatedShop.get().getName(), is(equalTo("BIEDRONKA"))),
              () -> assertThat(actualUpdatedShop.get().getCountryDto().getName(), is(equalTo("GERMANY")))

      );
    }, "Shop properties (name) should be properly updated, country name should remain constant");

    then(countryRepository).should(times(1)).findCountryByName(argumentCaptor.getValue().getCountry().getName());
    then(shopRepository).should(times(1)).addOrUpdate(updatedShop);


  }

  @Test
  @DisplayName("updateProduct shop country name")
  void test9() {

    //given
    ShopDto shopDtoToUpdate = ShopDto.builder()
            .id(1L)
            .name("MINIMAL")
            .countryDto(CountryDto.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();

    Map<ShopField, String> shopNewPropertyValues = Map.of(
            ShopField.COUNTRY, "POLAND"
    );


    Shop updatedShop = Shop.builder()
            .id(1L)
            .name("MINIMAL")
            .country(Country.builder()
                    .id(2L)
                    .name("POLAND")
                    .build())
            .build();


    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.addOrUpdate(argumentCaptor.capture()))
            .willReturn(Optional.of(updatedShop));

    given(countryRepository.findCountryByName("POLAND")).willReturn(Optional.of(Country.builder().id(2L).name("POLAND").build()));

    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<ShopDto> actualUpdatedShop = shopService.update(shopDtoToUpdate, shopNewPropertyValues);
      assertAll(
              () -> assertThat(actualUpdatedShop, is(equalTo(Optional.of(updatedShop).map(ModelMapper::mapShopToShopDto)))),
              () -> assertThat(argumentCaptor.getValue(), is(equalTo(updatedShop))),
              () -> assertThat(actualUpdatedShop.isPresent(), is(true)),
              () -> assertThat(actualUpdatedShop.get().getName(), is(equalTo("MINIMAL"))),
              () -> assertThat(actualUpdatedShop.get().getCountryDto().getName(), is(equalTo("POLAND")))

      );
    }, "Shop properties (name) should be properly updated, country name should remain constant");

    then(countryRepository).should(times(1)).findCountryByName(argumentCaptor.getValue().getCountry().getName());
    then(shopRepository).should(times(1)).addOrUpdate(updatedShop);

  }

  @Test
  @DisplayName("updateProduct nothing")
  void test10() {

    //given
    ShopDto shopDtoToUpdate = ShopDto.builder()
            .id(1L)
            .name("MINIMAL")
            .countryDto(CountryDto.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();

    Map<ShopField, String> shopNewPropertyValues = Collections.emptyMap();

    Shop updatedShop = Shop.builder()
            .id(1L)
            .name("MINIMAL")
            .country(Country.builder()
                    .id(1L)
                    .name("GERMANY")
                    .build())
            .build();


    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.addOrUpdate(argumentCaptor.capture()))
            .willReturn(Optional.of(updatedShop));

    given(countryRepository.findCountryByName("GERMANY")).willReturn(Optional.of(Country.builder().id(1L).name("GERMANY").build()));

    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<ShopDto> actualUpdatedShop = shopService.update(shopDtoToUpdate, shopNewPropertyValues);
      assertAll(
              () -> assertThat(actualUpdatedShop, is(equalTo(Optional.of(updatedShop).map(ModelMapper::mapShopToShopDto)))),
              () -> assertThat(argumentCaptor.getValue(), is(equalTo(updatedShop))),
              () -> assertThat(actualUpdatedShop.isPresent(), is(true)),
              () -> assertThat(actualUpdatedShop.get().getName(), is(equalTo("MINIMAL"))),
              () -> assertThat(actualUpdatedShop.get().getCountryDto().getName(), is(equalTo("GERMANY")))

      );
    }, "Shop properties (name) should be properly updated, country name should remain constant");

    then(countryRepository).should(times(1)).findCountryByName(argumentCaptor.getValue().getCountry().getName());
    then(shopRepository).should(times(1)).addOrUpdate(updatedShop);
  }

  @Test
  @DisplayName("updateProduct method - bad input arguments")
  void test14() {

    //given
    ShopDto shopDtoToUpdate = null;
    Map<ShopField, String> shopNewPropertyValues = null;
    String expectedExceptionMessage = String.format("Shop updateProduct method: inputs arguments are wrong (shopToUpdate: %s, shopNewPropertyValues: %s)", shopDtoToUpdate, shopNewPropertyValues);

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> shopService.update(shopDtoToUpdate, shopNewPropertyValues));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(countryRepository).should(never()).findCountryByName(any());
    then(shopRepository).should(never()).addOrUpdate(any());

  }

  @Test
  @DisplayName("updateProduct method - shopDto's ID is null")
  void test15() {

    //given
    ShopDto shopDtoToUpdate = ShopDto.builder()
            .name("WALLMART")
            .build();

    Map<ShopField, String> shopNewPropertyValues = Map.of(
            ShopField.NAME, "MART"
    );
    String expectedExceptionMessage = "You cannot updateProduct a not persisted entity!";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> shopService.update(shopDtoToUpdate, shopNewPropertyValues));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(countryRepository).should(never()).findCountryByName(any());
    then(shopRepository).should(never()).addOrUpdate(any());

  }

  @Test
  @DisplayName("chooseShopToUpdate null shop id")
  void test16() {

    //given
    Long shopId = null;
    String expectedExceptionMessage = "Wrong argument - shopId is null";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> shopService.chooseShopToUpdate(shopId));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(shopRepository).should(never()).findById(anyLong());

  }

  @Test
  @DisplayName("chooseShopToUpdate")
  void test17() {

    //given
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    Optional<Shop> expectedShop = Optional.of(Shop.builder()
            .id(5L)
            .name("WALLMART")
            .country(Country.builder()
                    .id(1L)
                    .name("USA")
                    .build())
            .build());

    given(shopRepository.findById(argumentCaptor.capture())).willReturn(expectedShop);

    //when
    //then
    assertDoesNotThrow(() -> {
      ShopDto actualShopDto = shopService.chooseShopToUpdate(5L);
      assertThat(actualShopDto, is(equalTo(expectedShop.map(ModelMapper::mapShopToShopDto).get())));
    });
    then(shopRepository).should(times(1)).findById(5L);

  }

  @Test
  @DisplayName("delete shop with null argument - shopDto should throw an exception with appropriate message")
  void test11() {

    //given
    ShopDto shopDto = null;
    String expectedExceptionMessage = "Shop is null/ undefined";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> shopService.deleteShopDto(shopDto));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(shopRepository).should(never()).deleteShop(ModelMapper.mapShopDtoToShop(shopDto));
  }

  @Test
  @DisplayName("delete shop with non null value should not throw an exception")
  void test12() {

    //given
    ShopDto shopDto = ShopDto.builder()
            .id(1L)
            .name("SHOP")
            .countryDto(CountryDto.builder()
                    .id(5L)
                    .name("COUNTRY")
                    .build())
            .build();

    //when
    //then
    assertDoesNotThrow(() -> shopService.deleteShopDto(shopDto));
    then(shopRepository).should(times(1)).deleteShop(ModelMapper.mapShopDtoToShop(shopDto));
  }

  @Test
  @DisplayName("get all shops")
  void test13() {

    //given
    List<Shop> returnShopList = List.of(
            Shop.builder()
                    .id(1L)
                    .name("SHOP UNO")
                    .country(Country.builder()
                            .name("COUNTRY UNO")
                            .build())
                    .build(),

            Shop.builder()
                    .name("SHOP DUE")
                    .country(Country.builder()
                            .name("COUNTRY DUE")
                            .build())
                    .build()

    );
    given(shopRepository.findAll()).willReturn(returnShopList);

    List<ShopDto> expectedShopDtoList = returnShopList
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());

    //when
    //then
    assertThat(shopService.getAllShops(), is(equalTo(expectedShopDtoList)));
    then(shopRepository).should(times(1)).findAll();
  }
}
