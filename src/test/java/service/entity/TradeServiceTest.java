package service.entity;

import domain.Trade;
import dto.TradeDto;
import exception.AppException;
import mapper.ModelMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.TradeRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Tag("Services")
@DisplayName("Test cases for TradeService")
class TradeServiceTest {

  @Mock
  private TradeRepository tradeRepository;

  @InjectMocks
  private TradeService tradeService;

  @Test
  @DisplayName("addTrade to Db with null argument should throw an exception with appropriate message")
  void test1() {

    //given
    TradeDto tradeDto = null;
    String expectedExceptionMessage = "TradeDto object is null";

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> tradeService.addTradeToDbFromUserInput(tradeDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
  }

  @Test
  @DisplayName("addTrade to DB with empty String tradeDto name should throw an exception")
  void test2() {

    //given
    TradeDto tradeDto = TradeDto.builder()
            .name("")
            .build();

    String expectedExceptionMessage = String.format("Trade name is undefined/null: %s", tradeDto.getName());

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> tradeService.addTradeToDbFromUserInput(tradeDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
  }

  @Test
  @DisplayName("addTrade to DB with not unique tradeDto should throw an exception")
  void test3() {

    //given
    TradeDto tradeDto = TradeDto.builder()
            .name("TRADE")
            .build();

    String expectedExceptionMessage = "Couldn't add a trade to db - trade's not unique by name: " + tradeDto.getName();

    given(tradeRepository.findTradeByName(tradeDto.getName())).willReturn(Optional.of(
            Trade.builder()
                    .id(1L)
                    .name(tradeDto.getName())
                    .build()));
    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> tradeService.addTradeToDbFromUserInput(tradeDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));

  }

  @Test
  @DisplayName("add unique trade with appropriate name should not throw an exception")
  void test4() {

    //given
    TradeDto tradeDto = TradeDto.builder()
            .name("TRADE")
            .build();
    given(tradeRepository.findTradeByName(tradeDto.getName())).willReturn(Optional.empty());

    //when
    //then
    assertDoesNotThrow(() -> tradeService.addTradeToDbFromUserInput(tradeDto));
  }

  @Test
  @DisplayName("get Trade from db if exists with null argument should throw an appropriate exception")
  void test5() {

    //given
    TradeDto tradeDto = null;
    String expectedExceptionMessage = "TradeDto object is null";

    //when
    //then
    AppException thrownException = assertThrows(AppException.class, () -> tradeService.getTradeFromDbIfExists(tradeDto));
    assertThat(thrownException.getMessage(), is(equalTo(expectedExceptionMessage)));
  }

  @Test
  @DisplayName("getTrade from db with valid argument should return appropriate value")
  void test6() {

    //given
    TradeDto tradeDto = TradeDto.builder()
            .name("TRADE")
            .build();

    //when
    //then
    assertDoesNotThrow(() -> {
      TradeDto actualResult = tradeService.getTradeFromDbIfExists(tradeDto);
      assertThat(actualResult, not(nullValue()));
      assertThat(actualResult, is(equalTo(tradeDto)));
    });
  }

  @ParameterizedTest
  @MethodSource("getNotValidTradeName")
  @DisplayName("delete category by name with null or empty name should throw an exception with appropriate message")
  void test8(String name) {

    //given
    String expectedExceptionMessage = "Trade name is null/ undefined: " + name;

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> tradeService.deleteTradeByName(name));
    assertThat(appException.getMessage(), Matchers.is(equalTo(expectedExceptionMessage)));

  }

  @TestFactory
  @DisplayName("delete trade by name with valid name should not throw an exception")
  Collection<DynamicTest> test9() {

    return new Random().ints(10, 1, 11).boxed()
            .map(num -> {
              String generatedName = getRandomStringWithUpperCaseLetters(num);
              return DynamicTest.dynamicTest(generatedName, () -> assertDoesNotThrow(() -> tradeService.deleteTradeByName(generatedName)));
            })
            .collect(Collectors.toList());
  }

  private static List<String> getNotValidTradeName() {
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


  @Test
  @DisplayName("get All Trade method")
  void test10() {

    //given
    given(tradeRepository.findAll())
            .willReturn(List.of(

                    Trade.builder()
                            .name("TRADE A")
                            .build(),

                    Trade.builder()
                            .name("TRADE B")
                            .build()
            ));

    List<TradeDto> expectedResultList = List.of(
            TradeDto.builder()
                    .name("TRADE A")
                    .build(),

            TradeDto.builder()
                    .name("TRADE B")
                    .build()
    );

    //when
    //then
    assertDoesNotThrow(() -> {
      List<TradeDto> actualResultList = tradeService.getAllTrades();
      assertThat(actualResultList, is(equalTo(expectedResultList)));
    });

  }
}
