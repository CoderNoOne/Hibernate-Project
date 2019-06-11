package service;

import domain.Customer;
import exception.AppException;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;
import utils.entity_utils.CustomerUtil;
import validator.impl.CustomerValidator;

import java.util.Optional;
import java.util.stream.Collectors;

import static utils.UserDataUtils.printMessage;


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

  public Customer getValidatedCustomer(){

    var customer = CustomerUtil.createCustomerFromUserInput();
    var errorsMap = customerValidator.validate(customer);

    if (customerValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Customer is not valid");
    }

    return customer;
  }

}
