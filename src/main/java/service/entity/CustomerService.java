package service.entity;

import domain.Country;
import domain.Customer;
import exception.AppException;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.CustomerRepositoryImpl;


import java.util.Optional;


public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CountryService countryService;

  public CustomerService() {
    customerRepository = new CustomerRepositoryImpl();
    countryService = new CountryService();
  }

  public Optional<Customer> addCustomerToDb(Customer customer) {
    return customerRepository.addOrUpdate(customer);
  }

  private Country getCountryFromDbIfExists(Country country) {
    return countryService.getCountryByName(country.getName()).orElse(country);
  }

  private Customer setCustomerComponentsFromDbIfTheyExist(Customer customer) {

    return Customer.builder()
            .name(customer.getName())
            .surname(customer.getSurname())
            .age(customer.getAge())
            .country(getCountryFromDbIfExists(customer.getCountry()))
            .build();
  }


  public void addCustomerToDbFromUserInput(Customer customer) {
    if (isCustomerUniqueByNameAndSurnameAndCountry(customer.getName(), customer.getSurname(), customer.getCountry())) {
      addCustomerToDb(setCustomerComponentsFromDbIfTheyExist(customer));
    } else {
      throw new AppException("Couldn't add customer to db - customer: " + customer + "is not unique by name, surname and country");
    }
  }

  private boolean isCustomerUniqueByNameAndSurnameAndCountry(String name, String surname, Country country) {
    if (country == null) {
      throw new AppException("Customer is null");
    }

    return getCustomerByNameAndSurnameAndCountry(name, surname, country).isEmpty();

  }

  public Optional<Customer> getCustomerByNameAndSurnameAndCountry(String name, String surname, Country country) {
    return customerRepository.findByNameAndSurnameAndCountry(name, surname, country);
  }

  public void deleteAllCustomers(){
    customerRepository.deleteAll();
  }

}
