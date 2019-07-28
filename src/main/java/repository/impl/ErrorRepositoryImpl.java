package repository.impl;

import domain.Error;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ErrorRepository;

public class ErrorRepositoryImpl extends AbstractCrudRepository<Error, Long> implements ErrorRepository {
}
