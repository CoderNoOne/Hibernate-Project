package service.entity;

import dto.ErrorDto;
import mappers.ErrorMapper;
import repository.abstract_repository.entity.ErrorRepository;
import repository.impl.ErrorRepositoryImpl;
import validator.impl.ErrorDtoValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ErrorService {

  private final ErrorRepository errorRepository;
  private final ErrorDtoValidator errorValidator;
  private final ErrorMapper errorMapper;

  public ErrorService() {
    this.errorRepository = new ErrorRepositoryImpl();
    this.errorValidator = new ErrorDtoValidator();
    this.errorMapper = new ErrorMapper();
  }

  public Optional<ErrorDto> addErrorToDb(ErrorDto errorDto) {

    return errorRepository
            .addOrUpdate(errorMapper.mapErrorDtoToError(errorDto))
            .map(errorMapper::mapErrorToErrorDto);
  }

  public void deleteAllErrors() {
    errorRepository.deleteAll();
  }

  public List<ErrorDto> getAllErrors() {
    return errorRepository.findAll()
            .stream()
            .map(errorMapper::mapErrorToErrorDto)
            .collect(Collectors.toList());
  }
}
