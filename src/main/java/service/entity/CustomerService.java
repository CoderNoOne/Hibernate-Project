package service.entity;

import domain.Customer;
import dto.CountryDto;
import dto.CustomerDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.CustomerRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.CustomerRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.CustomerUtil.getCustomerDtoIfValid;

@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CountryRepository countryRepository;

  public CustomerService() {
    this.customerRepository = new CustomerRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public Optional<CustomerDto> addCustomerToDb(CustomerDto customerDto) {

    Customer customer = ModelMapper.mapCustomerDtoToCustomer(customerDto);

    countryRepository
            .findCountryByName(customerDto.getCountryDto().getName())
            .ifPresentOrElse(country -> {
              customer.setCountry(country);
              customerRepository.add(customer);
            }, () -> {
              countryRepository.add(customer.getCountry());
              customerRepository.add(customer);
            });

    return Optional.of(ModelMapper.mapCustomerToCustomerDto(customer));
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
      addCustomerToDb(/*setCustomerComponentsFromDbIfTheyExist*/(customerDto));
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

  public List<CustomerDto> getAllCustomers() {
    return customerRepository.findAll()
            .stream()
            .map(ModelMapper::mapCustomerToCustomerDto)
            .collect(Collectors.toList());
  }

  public Optional<CustomerDto> getCustomerById(Long customerId) {

    if (customerId == null) {
      throw new AppException("getCustomerById method - Customer id is null");
    }

    return customerRepository.findById(customerId)
            .map(ModelMapper::mapCustomerToCustomerDto);
  }


  public Optional<CustomerDto> updateCustomer(CustomerDto customerDtoToUpdate) {

    Long id = customerDtoToUpdate.getId();

    if (id == null) {
      throw new AppException("Customer id is null");
    }

    Customer customerFromDb = customerRepository.findById(id)
            .orElseThrow(() -> new AppException("Customer with id: " + id + " doesn't exist in DB yet"));

    CustomerDto customerToUpdate = CustomerDto.builder()
            .id(id)
            .age(customerDtoToUpdate.getAge() != null ? customerDtoToUpdate.getAge() : customerFromDb.getAge())
            .name(customerDtoToUpdate.getName() != null ? customerDtoToUpdate.getName() : customerFromDb.getName())
            .surname(customerDtoToUpdate.getSurname() != null ? customerDtoToUpdate.getSurname() : customerFromDb.getSurname())
            .countryDto(customerDtoToUpdate.getCountryDto() != null ? customerDtoToUpdate.getCountryDto() : ModelMapper.mapCountryToCountryDto(customerFromDb.getCountry()))
            .build();

    return customerRepository
            .add(ModelMapper.mapCustomerDtoToCustomer(getCustomerDtoIfValid(setCustomerComponentsFromDbIfTheyExist(customerToUpdate))))
            .map(ModelMapper::mapCustomerToCustomerDto);
  }
}
