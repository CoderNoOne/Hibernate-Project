package service.entity;

import dto.CountryDto;
import dto.CustomerDto;
import exception.AppException;
import mappers.CountryMapper;
import mappers.CustomerMapper;
import repository.abstract_repository.entity.CustomerRepository;
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
  private final CountryService countryService;
  private final ProductService productService;
  private final CustomerMapper customerMapper;
  private final CountryMapper countryMapper;

  public CustomerService() {
    this.customerRepository = new CustomerRepositoryImpl();
    this.countryService = new CountryService();
    this.productService = new ProductService();
    this.customerMapper = new CustomerMapper();
    this.countryMapper = new CountryMapper();
  }

  public Optional<CustomerDto> addCustomerToDb(CustomerDto customerDto) {
    return customerRepository
            .addOrUpdate(customerMapper.mapCustomerDtoToCustomer(customerDto))
            .map(customerMapper::mapCustomerToCustomerDto);
  }

  private CustomerDto setCustomerComponentsFromDbIfTheyExist(CustomerDto customerDto) {

    return CustomerDto.builder()
            .id(customerDto.getId())
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .age(customerDto.getAge())
            .countryDto(countryService.getCountryFromDbIfExists(customerDto.getCountryDto()))
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

  public Optional<CustomerDto> getCustomerByNameAndSurnameAndCountry(String name, String surname, CountryDto countryDto) {
    return customerRepository.findByNameAndSurnameAndCountry(name, surname, countryMapper.mapCountryDtoToCountry(countryDto))
            .map(customerMapper::mapCustomerToCustomerDto);
  }

  public void deleteAllCustomers() {
    customerRepository.deleteAll();
  }

  public void deleteCustomer(CustomerDto customerDto) {

    if (customerDto == null) {
      throw new AppException("Customer object you wanted to delete is null");
    }

    customerRepository.deleteCustomer(customerMapper.mapCustomerDtoToCustomer(customerDto));
  }

  public List<CustomerDto> getAllCustomers() {
    return customerRepository.findAll()
            .stream()
            .map(customerMapper::mapCustomerToCustomerDto)
            .collect(Collectors.toList());
  }

  public Optional<CustomerDto> getCustomerById(Long customerId) {
    return customerRepository.findById(customerId)
            .map(customerMapper::mapCustomerToCustomerDto);
  }

  public void updateCustomer() {
    printCollectionWithNumeration(getAllCustomers());
    long customerId = getInt("Choose customer id you want to update");

    getCustomerById(customerId)
            .ifPresentOrElse(customerDto ->
                            customerRepository
                                    .addOrUpdate(customerMapper
                                            .mapCustomerDtoToCustomer(
                                                    setCustomerComponentsFromDbIfTheyExist(
                                                            getCustomerDtoIfValid(getUpdatedCustomerDto(customerDto))))),
                    () -> {
                      throw new AppException("There is no customer with that id: " + customerId + " in DB");
                    });

  }

  public CustomerDto getCustomerDtoFromDbIfExists(CustomerDto customerDto) {
    return getCustomerByNameAndSurnameAndCountry(customerDto.getName(), customerDto.getSurname(), customerDto.getCountryDto()).orElse(customerDto);
  }
}
