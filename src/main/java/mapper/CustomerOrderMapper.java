package mapper;

import domain.CustomerOrder;
import dto.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerOrderMapper {

  CustomerOrder customerOrderDtoToCustomerOrder(CustomerOrderDto customerOrderDto);

  CustomerOrderDto customerOrderToCustomerOrderDto(CustomerOrder customerOrder);

}
