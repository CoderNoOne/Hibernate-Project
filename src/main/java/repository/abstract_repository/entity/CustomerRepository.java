package repository.abstract_repository.entity;


import domain.Country;
import domain.Customer;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;


public interface CustomerRepository extends CrudRepository<Customer, Long> {

  Optional<Customer> findByNameAndSurnameAndCountry(String name, String surname, Country country);

  void deleteCustomer(Customer customerToDelete);
}
