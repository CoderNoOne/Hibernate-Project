package service;

import domain.Country;
import domain.Customer;
import exception.AppException;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;


import java.util.Optional;


public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService() {
    customerRepository = new CustomerRepositoryImpl();
  }

  public Optional<Customer> addCustomerToDb(Customer customer) {
    return customerRepository.addOrUpdate(customer);
  }

  public void addCustomerToDbFromUserInput(Customer customer) {
    if (isCustomerUniqueByNameAndSurnameAndCountry(customer.getName(), customer.getSurname(), customer.getCountry())) {
      addCustomerToDb(customer);
    } else {
      throw new AppException("Couldn't add customer to db - customer's not unique by name, surname and country");
    }
  }

  private boolean isCustomerUniqueByNameAndSurnameAndCountry(String name, String surname, Country country) {
    if (country == null) {
      throw new AppException("Customer is null");
    }

    return getCustomerByNameAndSurnameAndCountry(name, surname, country).isEmpty();
         /*   || !customerRepository.findByNameAndSurnameAndCountry(name, surname, country).get()
            .getCountry().getName().equals(country.getName());
*/
  }

  public Optional<Customer> getCustomerByNameAndSurnameAndCountry(String name, String surname, Country country){
    return customerRepository.findByNameAndSurnameAndCountry(name, surname, country);
  }
}
