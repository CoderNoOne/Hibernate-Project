package service.entity;

import domain.Country;
import domain.Customer;
import exception.AppException;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;


import java.util.List;
import java.util.Optional;

import static util.entity_utils.CustomerUtil.getCustomerIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateCustomerUtil.getUpdatedCustomer;


public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CountryService countryService;
  private final ProductService productService;

  public CustomerService() {
    this.customerRepository = new CustomerRepositoryImpl();
    this.countryService = new CountryService();
    this.productService = new ProductService();
  }

  public Optional<Customer> addCustomerToDb(Customer customer) {
    return customerRepository.addOrUpdate(customer);
  }

  private Customer setCustomerComponentsFromDbIfTheyExist(Customer customer) {

    return Customer.builder()
            .id(customer.getId())
            .name(customer.getName())
            .surname(customer.getSurname())
            .age(customer.getAge())
            .country(countryService.getCountryFromDbIfExists(customer.getCountry()))
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

  public void deleteAllCustomers() {
    customerRepository.deleteAll();
  }

  public void deleteCustomer(Customer customerToDelete) {

    if (customerToDelete == null) {
      throw new AppException("Customer object you wanted to delete is null");
    }

    customerRepository.deleteCustomer(customerToDelete);
  }


  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  public Optional<Customer> getCustomerById(Long customerId) {
    return customerRepository.findById(customerId);
  }

  public void updateCustomer() {
    printCollectionWithNumeration(getAllCustomers());
    long customerId = getInt("Choose customer id you want to update");

    getCustomerById(customerId)
            .ifPresentOrElse(customer ->
                            customerRepository.addOrUpdate(setCustomerComponentsFromDbIfTheyExist(getCustomerIfValid(getUpdatedCustomer(customer)))),
                    () -> {
                      throw new AppException("There is no customer with that id: " + customerId + " in DB");
                    });

  }

  public Customer getCustomerFromDbIfExists(Customer customer) {
    return getCustomerByNameAndSurnameAndCountry(customer.getName(), customer.getSurname(), customer.getCountry()).orElse(customer);
  }
}
