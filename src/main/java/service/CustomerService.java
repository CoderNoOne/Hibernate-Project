package service;

import domain.Customer;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;
import validator.impl.CustomerValidator;

import java.util.Optional;


public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerValidator customerValidator;

  public CustomerService() {
    customerRepository = new CustomerRepositoryImpl();
    customerValidator = new CustomerValidator();
  }

  public Optional<Customer> addCustomerToDb(Customer customer) {

    return customerRepository.addOrUpdate(customer);
  }

}
