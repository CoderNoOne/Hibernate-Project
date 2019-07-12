package custom_mappers;

import domain.Country;
import domain.Customer;
import dto.CountryDto;
import dto.CustomerDto;

public class CustomerMapper {

  public CustomerDto mapCustomerToCustomerDto(Customer customer) {

    return CustomerDto.builder()
            .id(customer.getId())
            .age(customer.getAge())
            .name(customer.getName())
            .surname(customer.getSurname())
            .countryDto(CountryDto.builder()
                    .id(customer.getCountry().getId())
                    .name(customer.getCountry().getName())
                    .build()
            ).build();
  }

  public Customer mapCustomerDtoToCustomer(CustomerDto customerDto) {

    return Customer.builder()
            .id(customerDto.getId())
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .age(customerDto.getAge())
            .country(Country.builder()
                    .id(customerDto.getCountryDto().getId())
                    .name(customerDto.getCountryDto().getName())
                    .build())
            .build();
  }

}
