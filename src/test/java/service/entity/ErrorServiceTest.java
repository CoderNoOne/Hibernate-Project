package service.entity;

import domain.Error;
import dto.ErrorDto;
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
import repository.abstract_repository.entity.ErrorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Tag("Services")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@DisplayName("Test cases for ErrorService")
class ErrorServiceTest {


  @Mock
  private ErrorRepository errorRepository;

  @InjectMocks
  private ErrorService errorService;

  @Test
  @DisplayName("addErrorToDb ErrorDto object is null")
  void test1() {

    //given
    ErrorDto errorDto = null;
    String expectedExceptionMessage = "ErrorDto object is null";

    //when
    //then
    AppException appException = assertThrows(AppException.class, () -> errorService.addErrorToDb(errorDto));
    assertThat(appException.getMessage(), is(equalTo(expectedExceptionMessage)));
    then(errorRepository).should(never()).addOrUpdate(any());
  }

  @Test
  @DisplayName("addErrorToDb non null argument")
  void test2() {


    //given
    LocalDateTime now = LocalDateTime.now();
    String errorMessage = "Message error";

    ErrorDto errorDto = ErrorDto.builder()
            .date(now)
            .message(errorMessage)
            .build();

    Optional<ErrorDto> expectedResult = Optional.of(ErrorDto.builder()
            .date(now)
            .message(errorMessage)
            .build());

    ArgumentCaptor<Error> errorArgumentCaptor = ArgumentCaptor.forClass(Error.class);
    given(errorRepository.addOrUpdate(errorArgumentCaptor.capture()))
            .willReturn(Optional.ofNullable(ModelMapper.mapErrorDtoToError(errorDto)));

    //when
    //then

    assertDoesNotThrow(() -> {
      Optional<ErrorDto> actualResult = errorService.addErrorToDb(errorDto);
      assertThat(actualResult, is(equalTo(expectedResult)));
    });

    System.out.println(errorArgumentCaptor.getValue());
    then(errorRepository).should(times(1)).addOrUpdate(ModelMapper.mapErrorDtoToError(errorDto));
  }

  @Test
  @DisplayName("getAllErrors")
  void test3() {

    //given

    List<ErrorDto> expectedResult = List.of(
            ErrorDto.builder()
                    .id(1L)
                    .message("Error message 1")
                    .date(LocalDateTime.of(2016, 10, 20, 6, 10, 0))
                    .build(),

            ErrorDto.builder()
                    .id(2L)
                    .message("Error message 2")
                    .date(LocalDateTime.of(2017, 5, 13, 20, 30, 30))
                    .build()

    );

    given(errorRepository.findAll())
            .willReturn(expectedResult.stream()
                    .map(ModelMapper::mapErrorDtoToError)
                    .collect(Collectors.toList()));

    //when
    //then
    assertDoesNotThrow(() -> {
      List<ErrorDto> actualErrors = errorService.getAllErrors();
      assertThat(actualErrors,is(equalTo(expectedResult)));
    });

    then(errorRepository).should(times(1)).findAll();
  }
}
