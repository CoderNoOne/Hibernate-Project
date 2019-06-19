package service;

import domain.CustomerOrder;
import domain.Product;
import dto.CategoryDTO;
import dto.ProductDTO;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

  public Map<Product, Integer> getNumberOfOrdersForSpecifiedProducts(List<Product> productList) {
    return customerOrderRepository.findNumberOfOrdersForSpecifiedProducts(productList);
  }

  public Map<CategoryDTO, Map<ProductDTO, Integer>> getTheMostExpensiveProductsInEachCategoryWithAmountOfProductSales() {

    return customerOrderRepository.findTheMostExpensiveProductInEachCategoryWithNumberOfPurchases().entrySet().stream()
            .collect(Collectors.toMap(
                    category -> C,
            ))


  }
}
