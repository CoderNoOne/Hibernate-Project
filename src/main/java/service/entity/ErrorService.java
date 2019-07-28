package service.entity;

import dto.ErrorDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.ErrorRepository;
import repository.impl.ErrorRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ErrorService {

  private final ErrorRepository errorRepository;

  public ErrorService() {
    this.errorRepository = new ErrorRepositoryImpl();
  }

  public Optional<ErrorDto> addErrorToDb(ErrorDto errorDto) {

    if (errorDto == null) {
      throw new AppException("ErrorDto object is null");
    }
    return errorRepository
            .addOrUpdate(ModelMapper.mapErrorDtoToError(errorDto))
            .map(ModelMapper::mapErrorToErrorDto);
  }

  public void deleteAllErrors() {
    errorRepository.deleteAll();
  }

  public List<ErrorDto> getAllErrors() {
    return errorRepository.findAll()
            .stream()
            .map(ModelMapper::mapErrorToErrorDto)
            .collect(Collectors.toList());
  }
}
