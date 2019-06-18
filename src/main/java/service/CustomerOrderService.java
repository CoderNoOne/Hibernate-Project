package service;

import domain.CustomerOrder;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.util.Optional;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
  }

  private Optional<CustomerOrder> addCustomerOrderToDb(CustomerOrder customerOrder) {
    return customerOrderRepository.addOrUpdate(customerOrder);
  }

  public void addCustomerOrderToDbFromUserInput(CustomerOrder customerOrder) {
    addCustomerOrderToDb(customerOrder);
  }
}
