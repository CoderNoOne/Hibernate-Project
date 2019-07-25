package service.entity;

import domain.Country;
import domain.Customer;
import dto.CountryDto;
import dto.CustomerDto;
import mapper.ModelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.CustomerRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Tag("Services")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@DisplayName("Test cases for CustomerService")
class CustomerServiceTest {

  @Mock
  CustomerRepository customerRepository;

  @Mock
  CountryRepository countryRepository;

  @InjectMocks
  CustomerService customerService;

  @Test
  @DisplayName("updateProduct customer: updateProduct customer country name")
  void test1() {

    //given
    CustomerDto customerDtoToUpdate = CustomerDto.builder()
            .id(2L)
            .countryDto(CountryDto.builder()
                    .name("BRAZIL")
                    .build())
            .build();


    Optional<Customer> customerFromDb = Optional.of(Customer.builder()
            .id(2L)
            .name("JACK")
            .surname("WALKER")
            .age(50)
            .country(Country.builder()
                    .id(3L)
                    .name("WALES")
                    .build())
            .build());


    Customer customerToUpdate = Customer.builder()
            .id(2L)
            .name(customerFromDb.get().getName())
            .surname(customerFromDb.get().getSurname())
            .age(customerFromDb.get().getAge())
            .country(Country.builder()
                    .name("BRAZIL")
                    .build())
            .build();

    Optional<Customer> expectedUpdatedCustomer = Optional.of(
            Customer.builder()
                    .id(2L)
                    .name("JOHN")
                    .surname("WALKER")
                    .age(50)
                    .country(Country.builder()
                            .id(3L)
                            .name("WALES")
                            .build())
                    .build());

    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Customer> argumentCaptorCustomer = ArgumentCaptor.forClass(Customer.class);
    ArgumentCaptor<String> countryNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

    given(customerRepository.findById(argumentCaptor.capture()))
            .willReturn(customerFromDb);

    given(customerRepository.addOrUpdate(argumentCaptorCustomer.capture()))
            .willReturn(expectedUpdatedCustomer);

    given(countryRepository.findCountryByName(countryNameArgumentCaptor.capture()))
            .willReturn(Optional.empty());

    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<CustomerDto> actualCustomerDto = customerService.updateCustomer(customerDtoToUpdate);
      assertThat(actualCustomerDto, is(equalTo(expectedUpdatedCustomer.map(ModelMapper::mapCustomerToCustomerDto))));
    });

    InOrder inOrder = inOrder(customerRepository);
    inOrder.verify(customerRepository, times(1)).findById(customerDtoToUpdate.getId());
    inOrder.verify(customerRepository, times(1)).addOrUpdate(customerToUpdate);

    then(countryRepository).should(times(1)).findCountryByName(customerToUpdate.getCountry().getName());
  }


  @Test
  @DisplayName("Update customer: updateProduct customer name")
  void test2() {

    //given
    CustomerDto customerDtoToUpdate = CustomerDto.builder()
            .id(2L)
            .name("JOHN")
            .build();


    Optional<Customer> customerFromDb = Optional.of(Customer.builder()
            .id(2L)
            .name("JACK")
            .surname("WALKER")
            .age(50)
            .country(Country.builder()
                    .id(3L)
                    .name("WALES")
                    .build())
            .build());


    Customer customerToUpdate = Customer.builder()
            .id(2L)
            .name("JOHN")
            .age(customerFromDb.get().getAge())
            .surname(customerFromDb.get().getSurname())
            .country(customerFromDb.get().getCountry())
            .build();

    Optional<Customer> expectedUpdatedCustomer = Optional.of(
            Customer.builder()
                    .id(2L)
                    .name("JOHN")
                    .surname("WALKER")
                    .age(50)
                    .country(Country.builder()
                            .id(3L)
                            .name("WALES")
                            .build())
                    .build());

    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Customer> argumentCaptorCustomer = ArgumentCaptor.forClass(Customer.class);

    given(customerRepository.findById(argumentCaptor.capture()))
            .willReturn(customerFromDb);

    given(customerRepository.addOrUpdate(argumentCaptorCustomer.capture()))
            .willReturn(expectedUpdatedCustomer);

    given(countryRepository.findCountryByName("WALES"))
            .willReturn(Optional.empty());
    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<CustomerDto> actualCustomerDto = customerService.updateCustomer(customerDtoToUpdate);
      assertThat(actualCustomerDto, is(equalTo(expectedUpdatedCustomer.map(ModelMapper::mapCustomerToCustomerDto))));
    });

    InOrder inOrder = inOrder(customerRepository);
    inOrder.verify(customerRepository, times(1)).findById(customerDtoToUpdate.getId());
    inOrder.verify(customerRepository, times(1)).addOrUpdate(customerToUpdate);

    then(countryRepository).should(times(1)).findCountryByName(customerToUpdate.getCountry().getName());
  }

  @Test
  @DisplayName("updateProduct customer: updateProduct customer age")
  void test3() {

    //given
    CustomerDto customerDtoToUpdate = CustomerDto.builder()
            .id(2L)
            .age(51)
            .build();


    Optional<Customer> customerFromDb = Optional.of(Customer.builder()
            .id(2L)
            .name("JACK")
            .surname("WALKER")
            .age(50)
            .country(Country.builder()
                    .id(3L)
                    .name("WALES")
                    .build())
            .build());


    Customer customerToUpdate = Customer.builder()
            .id(2L)
            .name(customerFromDb.get().getName())
            .age(51)
            .surname(customerFromDb.get().getSurname())
            .country(customerFromDb.get().getCountry())
            .build();

    Optional<Customer> expectedUpdatedCustomer = Optional.of(
            Customer.builder()
                    .id(2L)
                    .name("JOHN")
                    .surname("WALKER")
                    .age(50)
                    .country(Country.builder()
                            .id(3L)
                            .name("WALES")
                            .build())
                    .build());

    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Customer> argumentCaptorCustomer = ArgumentCaptor.forClass(Customer.class);

    given(customerRepository.findById(argumentCaptor.capture()))
            .willReturn(customerFromDb);

    given(customerRepository.addOrUpdate(argumentCaptorCustomer.capture()))
            .willReturn(expectedUpdatedCustomer);
    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<CustomerDto> actualCustomerDto = customerService.updateCustomer(customerDtoToUpdate);
      assertThat(actualCustomerDto, is(equalTo(expectedUpdatedCustomer.map(ModelMapper::mapCustomerToCustomerDto))));
    });

    InOrder inOrder = inOrder(customerRepository);
    inOrder.verify(customerRepository, times(1)).findById(customerDtoToUpdate.getId());
    inOrder.verify(customerRepository, times(1)).addOrUpdate(customerToUpdate);
  }

  @Test
  @DisplayName("updateProduct customer: updateProduct customer surname")
  void test4() {

    //given
    CustomerDto customerDtoToUpdate = CustomerDto.builder()
            .id(2L)
            .surname("TALKER")
            .build();


    Optional<Customer> customerFromDb = Optional.of(Customer.builder()
            .id(2L)
            .name("JACK")
            .surname("WALKER")
            .age(50)
            .country(Country.builder()
                    .id(3L)
                    .name("WALES")
                    .build())
            .build());


    Customer customerToUpdate = Customer.builder()
            .id(2L)
            .name(customerFromDb.get().getName())
            .age(customerFromDb.get().getAge())
            .surname("TALKER")
            .country(customerFromDb.get().getCountry())
            .build();

    Optional<Customer> expectedUpdatedCustomer = Optional.of(
            Customer.builder()
                    .id(2L)
                    .name("JOHN")
                    .surname("WALKER")
                    .age(50)
                    .country(Country.builder()
                            .id(3L)
                            .name("WALES")
                            .build())
                    .build());

    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Customer> argumentCaptorCustomer = ArgumentCaptor.forClass(Customer.class);

    given(customerRepository.findById(argumentCaptor.capture()))
            .willReturn(customerFromDb);

    given(customerRepository.addOrUpdate(argumentCaptorCustomer.capture()))
            .willReturn(expectedUpdatedCustomer);
    //when
    //then
    assertDoesNotThrow(() -> {
      Optional<CustomerDto> actualCustomerDto = customerService.updateCustomer(customerDtoToUpdate);
      assertThat(actualCustomerDto, is(equalTo(expectedUpdatedCustomer.map(ModelMapper::mapCustomerToCustomerDto))));
    });

    InOrder inOrder = inOrder(customerRepository);
    inOrder.verify(customerRepository, times(1)).findById(customerDtoToUpdate.getId());
    inOrder.verify(customerRepository, times(1)).addOrUpdate(customerToUpdate);
  }
}
