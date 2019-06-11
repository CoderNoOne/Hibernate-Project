package repository.impl;

import domain.Customer;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerRepository;

public class CustomerRepositoryImpl extends AbstractCrudRepository <Customer, Long> implements CustomerRepository {

}
