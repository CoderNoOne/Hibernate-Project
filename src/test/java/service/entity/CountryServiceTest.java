package service.entity;

import domain.Country;
import dto.CountryDto;
import exception.AppException;
import mapper.ModelMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import repository.abstract_repository.entity.CountryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Test cases for CountryService")
class CountryServiceTest {

  @Mock
  private CountryRepository countryRepository;

  @InjectMocks
  private CountryService countryService;

  @Test
  @DisplayName("add country to DB with null country Dto")
  void test1() {

    //given
    Country country = Country.builder()
            .id(1L)
            .name("POLAND")
            .build();

    given(countryRepository.addOrUpdate(ArgumentMatchers.argThat(c -> c.equals(country))))
            .willAnswer((Answer<Optional<Country>>) invocationOnMock -> Optional.of(Country.builder()
                    .id(invocationOnMock.getArgument(0, Country.class).getId())
                    .name(invocationOnMock.getArgument(0, Country.class).getName())
                    .build()));

    // when
    // then
    Assertions.assertDoesNotThrow(() -> {
      Optional<CountryDto> countryDto = countryService.addCountryToDb(ModelMapper.mapCountryToCountryDto(country));
      Assertions.assertAll(
              "CategoryDto test",
              () -> assertThat(countryDto.isPresent(), is(true)),
              () -> assertThat(countryDto.get().getName(), equalTo(country.getName())),
              () -> assertThat(countryDto.get().getId(), is(country.getId()))
      );

    });

  }

  @Test
  @DisplayName("add country to db with not unique country")
  void test2() {

    //given
    Country country = Country.builder()
            .id(1L)
            .name("POLAND")
            .build();

    AppException appException1 = new AppException("Country is not unique by name: " + country.getName());

    given(countryRepository.addOrUpdate(country))
            .willReturn(Optional.of(country));

    given(countryRepository.findCountryByName(country.getName())).willThrow(appException1);

    //when
    //then
    AppException appException2 = Assertions.assertThrows(AppException.class, () -> countryService.addCountryToDb(ModelMapper.mapCountryToCountryDto(country)));
    assertThat("exception message", appException2.getMessage(), equalTo(appException1.getMessage()));

    then(countryRepository).should(never()).addOrUpdate(any());
    then(countryRepository).should(times(1)).findCountryByName(country.getName());

  }


  @Test
  @DisplayName("getCountryFromDbIfExists - case: countryDto is null")
  void test3() {

    //given
    CountryDto countryDto = null;
    AppException appException1 = new AppException("CountryDto is null");
//    given(countryRepository.findCountryByName(anyString())).willReturn();

    //when
    //then
    AppException appException2 = Assertions.assertThrows(AppException.class, () -> countryService.getCountryFromDbIfExists(countryDto));
    assertThat(appException1.getMessage(), equalTo(appException2.getMessage()));


  }

  @Test
  @DisplayName("getCountryFromDbIfExists - case: countryDto name is null")
  void test4() {

    //given
    AppException appException1 = new AppException("Country name is null");
    CountryDto countryDto = CountryDto.builder()
            .name(null)
            .build();

    given(countryRepository.findCountryByName(countryDto.getName())).willThrow(appException1);

    //when
    //then
    AppException appException2 = Assertions.assertThrows(AppException.class, () -> countryService.getCountryFromDbIfExists(countryDto));
    assertThat(appException2.getMessage(), equalTo(appException1.getMessage()));
  }

  @Test
  @DisplayName("getCountryFromDBIfExists - case: countryDto is not null")
  void test5() {

    //given
    CountryDto countryDto = CountryDto.builder().name("POLAND").build();
    given(countryRepository.findCountryByName(countryDto.getName())).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      CountryDto countryFromDbIfExists = countryService.getCountryFromDbIfExists(countryDto);
      assertThat(countryDto, equalTo(countryFromDbIfExists));
    });
  }

  @Test
  @DisplayName("find all countries")
  void test6() {

    //given
    List<Country> countryList = List.of(
            Country.builder()
                    .id(1L)
                    .name("COMPUTERS")
                    .build(),
            Country.builder()
                    .id(2L)
                    .name("DRINKS")
                    .build());

    given(countryRepository.findAll()).willReturn(countryList);

    //when
    //then
    List<CountryDto> allCountries = countryService.findAllCountries();

    assertThat(allCountries, equalTo(countryList.stream().map(ModelMapper::mapCountryToCountryDto).collect(Collectors.toList())));
    assertThat(allCountries, not(empty()));
  }
}
