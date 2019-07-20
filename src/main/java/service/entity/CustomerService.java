package service.entity;

import dto.CountryDto;
import dto.CustomerDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.CustomerRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.CustomerUtil.getCustomerDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateCustomerUtil.getUpdatedCustomerDto;


public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CountryRepository countryRepository;

  public CustomerService() {
    this.customerRepository = new CustomerRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository) {
    this.customerRepository = customerRepository;
    this.countryRepository = countryRepository;

  }

  public Optional<CustomerDto> addCustomerToDb(CustomerDto customerDto) {
    return customerRepository
            .addOrUpdate(ModelMapper.mapCustomerDtoToCustomer(customerDto))
            .map(ModelMapper::mapCustomerToCustomerDto);
  }

  private CustomerDto setCustomerComponentsFromDbIfTheyExist(CustomerDto customerDto) {

    return CustomerDto.builder()
            .id(customerDto.getId())
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .age(customerDto.getAge())
            .countryDto(countryRepository.findCountryByName(customerDto.getCountryDto().getName())
                    .map(ModelMapper::mapCountryToCountryDto).orElse(customerDto.getCountryDto()))
            .build();
  }


  public void addCustomerToDbFromUserInput(CustomerDto customerDto) {
    if (isCustomerUniqueByNameAndSurnameAndCountry(customerDto.getName(), customerDto.getSurname(), customerDto.getCountryDto())) {
      addCustomerToDb(setCustomerComponentsFromDbIfTheyExist(customerDto));
    } else {
      throw new AppException("Couldn't add customer to db - customer: " + customerDto + "is not unique by name, surname and country");
    }
  }

  private boolean isCustomerUniqueByNameAndSurnameAndCountry(String name, String surname, CountryDto countryDto) {
    if (countryDto == null) {
      throw new AppException("Customer is null");
    }

    return getCustomerByNameAndSurnameAndCountry(name, surname, countryDto).isEmpty();

  }

  Optional<CustomerDto> getCustomerByNameAndSurnameAndCountry(String name, String surname, CountryDto countryDto) {
    return customerRepository.findByNameAndSurnameAndCountry(name, surname, ModelMapper.mapCountryDtoToCountry(countryDto))
            .map(ModelMapper::mapCustomerToCustomerDto);
  }

  public void deleteAllCustomers() {
    customerRepository.deleteAll();
  }

  public void deleteCustomer(CustomerDto customerDto) {

    if (customerDto == null) {
      throw new AppException("Customer object you wanted to delete is null");
    }

    customerRepository.deleteCustomer(ModelMapper.mapCustomerDtoToCustomer(customerDto));
  }

  private List<CustomerDto> getAllCustomers() {
    return customerRepository.findAll()
            .stream()
            .map(ModelMapper::mapCustomerToCustomerDto)
            .collect(Collectors.toList());
  }

  private Optional<CustomerDto> getCustomerById(Long customerId) {

    if (customerId == null) {
      throw new AppException("getCustomerById method - Customer id is null");
    }

    return customerRepository.findById(customerId)
            .map(ModelMapper::mapCustomerToCustomerDto);
  }

  public void updateCustomer() {
    printCollectionWithNumeration(getAllCustomers());
    long customerId = getInt("Choose customer id you want to update");

    getCustomerById(customerId)
            .ifPresentOrElse(customerDto ->
                            customerRepository
                                    .addOrUpdate(ModelMapper
                                            .mapCustomerDtoToCustomer(
                                                    setCustomerComponentsFromDbIfTheyExist(
                                                            getCustomerDtoIfValid(getUpdatedCustomerDto(customerDto))))),
                    () -> {
                      throw new AppException("There is no customer with that id: " + customerId + " in DB");
                    });

  }
}
