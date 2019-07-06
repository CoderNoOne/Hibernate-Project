package service.entity;

import repository.abstract_repository.entity.ErrorRepository;
import repository.impl.ErrorRepositoryImpl;
import domain.Error;
import validator.impl.ErrorValidator;

import java.util.List;
import java.util.Optional;

public class ErrorService {

  private final ErrorRepository errorRepository;
  private final ErrorValidator errorValidator;

  public ErrorService() {
    errorRepository = new ErrorRepositoryImpl();
    errorValidator = new ErrorValidator();
  }

  public Optional<Error> addErrorToDb(Error error) {

    return errorRepository.addOrUpdate(error);
  }

  public void deleteAllErrors() {
    errorRepository.deleteAll();
  }

  public List<Error> getAllErrors(){
    return errorRepository.findAll();
  }
}
