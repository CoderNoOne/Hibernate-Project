package repository.impl;

import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ErrorRepository;
import domain.Error;

public class ErrorRepositoryImpl extends AbstractCrudRepository<Error, Long> implements ErrorRepository {
}
