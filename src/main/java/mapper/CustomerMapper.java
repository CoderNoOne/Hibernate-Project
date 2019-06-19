package mapper;

import domain.Customer;
import dto.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

  Customer customerDtoToCustomer (CustomerDto customerDto);

  CustomerDto customerToCustomerDto (Customer customer);
}
