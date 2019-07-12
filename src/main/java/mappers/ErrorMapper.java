package mappers;

import domain.Error;
import dto.ErrorDto;

public class ErrorMapper {

  public Error mapErrorDtoToError(ErrorDto errorDto) {

    return Error.builder()
            .id(errorDto.getId())
            .message(errorDto.getMessage())
            .date(errorDto.getDate())
            .build();
  }

  public ErrorDto mapErrorToErrorDto(Error error) {

    return ErrorDto.builder()
            .id(error.getId())
            .message(error.getMessage())
            .date(error.getDate())
            .build();
  }


}
