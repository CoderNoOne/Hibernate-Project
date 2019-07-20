package service.entity;

import domain.Country;
import domain.Shop;
import dto.CountryDto;
import dto.ShopDto;
import exception.AppException;
import mapper.ModelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ShopRepository;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Tag("Services")
@DisplayName("Test cases for ShopService")
class ShopServiceTest {

  @Mock
  private ShopRepository shopRepository;

  @Mock
  private CountryRepository countryRepository;

  @InjectMocks
  private ShopService shopService;

  private static int counter;

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
  @DisplayName("update shop with valid arguments should not throw an exception. Updated Name should be properly set")
  void test7() {

    //given
    String input = String.join(System.lineSeparator(), "5", "NAME", "BIEDRONKA", "n");
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Shop shop = Shop.builder()
            .id(5L)
            .name("MINIMAL")
            .country(Country.builder()
                    .name("POLAND")
                    .build())
            .build();

    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.findById(argThat(num -> num == Long.parseLong(input.split("[\\s]")[0]))))
            .willReturn(Optional.of(
                    shop
            ));


    //when
    //then
    assertDoesNotThrow(shopService::updateShop);

    then(shopRepository).should(times(1)).addOrUpdate(argumentCaptor.capture());

    assertAll("Updated shop properties should be changed, not updated ones should remain constant",
            () -> assertThat(argumentCaptor.getValue().getName(), is(equalTo("BIEDRONKA"))),
            () -> assertThat(argumentCaptor.getValue().getCountry().getName(), is(equalTo("POLAND")))
    );
  }

  @Test
  @DisplayName("update shop with valid arguments should not throw an exception. Updated Country should be properly set")
  void test8() {

    //given
    String input = String.join(System.lineSeparator(), "2", "COUNTRY", "GERMANY", "n");
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Shop shop = Shop.builder()
            .name("REEBOK")
            .id(5L)
            .country(Country.builder()
                    .name("POLAND")
                    .build())
            .build();

    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.findById(argThat(num -> num == Long.parseLong(input.split("[\\s]")[0]))))
            .willReturn(Optional.of(
                    shop
            ));


    //when
    //then
    assertDoesNotThrow(shopService::updateShop);

    then(shopRepository).should(times(1)).addOrUpdate(argumentCaptor.capture());

    assertAll("Updated shop properties should be changed, not updated ones should remain constant",
            () -> assertThat(argumentCaptor.getValue().getName(), is(equalTo("REEBOK"))),
            () -> assertThat(argumentCaptor.getValue().getCountry().getName(), is(equalTo("GERMANY")))
    );

  }

  @Test
  @DisplayName("Update shop with valid arguments should not throw an exception. Updated Country and Shop Name shoould be properly ")
  void test9() {

    //given
    String input = String.join(System.lineSeparator(), "7", "COUNTRY", "GERMANY", "y", "NAME", "ADDIBAS", "n");
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Shop shop = Shop.builder()
            .name("REEBOK")
            .id(5L)
            .country(Country.builder()
                    .name("POLAND")
                    .build())
            .build();

    ArgumentCaptor<Shop> argumentCaptor = ArgumentCaptor.forClass(Shop.class);

    given(shopRepository.findById(argThat(num -> num == Long.parseLong(input.split("[\\s]")[0]))))
            .willReturn(Optional.of(
                    shop
            ));


    //when
    //then
    assertDoesNotThrow(shopService::updateShop);

    then(shopRepository).should(times(1)).addOrUpdate(argumentCaptor.capture());

    assertAll("Updated shop properties - country and name should be changed",
            () -> assertThat(argumentCaptor.getValue().getName(), is(equalTo("ADDIBAS"))),
            () -> assertThat(argumentCaptor.getValue().getCountry().getName(), is(equalTo("GERMANY")))
    );

  }

  @Test
  @DisplayName("delete shop with null argument - shopDto should throw an exception with appropriate message")
  void test10() {

    //given
    ShopDto shopDto = null;
    String expectedExceptionMessage = "Shop is null/ undefined";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> shopService.deleteShopDto(shopDto));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(shopRepository).should(never()).deleteShopDto(shopDto);
  }

  @Test
  @DisplayName("delete shop with non null value should not throw an exception")
  void test11() {

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
    then(shopRepository).should(times(1)).deleteShopDto(shopDto);
  }

  @Test
  @DisplayName("get all shops")
  void test12() {

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
