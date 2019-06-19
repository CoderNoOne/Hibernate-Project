package service;

import domain.CustomerOrder;
import domain.Product;
import dto.CategoryDTO;
import dto.CustomerOrderDto;
import dto.ProductDTO;
import mapper.CategoryMapper;
import mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;
  private final CategoryMapper categoryMapper;
  private final ProductMapper productMapper;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
    this.categoryMapper = Mappers.getMapper(CategoryMapper.class);
    this.productMapper = Mappers.getMapper(ProductMapper.class);
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

    return customerOrderRepository.findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases()
            .entrySet().stream().collect(Collectors.toMap(
                    e -> categoryMapper.categoryToCategoryDTO(e.getKey()),
                    e -> e.getValue().entrySet().stream().collect(Collectors.toMap(
                            ee -> productMapper.productToProductDTO(ee.getKey()),
                            Map.Entry::getValue
                    ))
            ));
  }

  public List<ProductDTO> getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinSpecifiedRangeAndSortedByPriceDescOrder(String countryName, Integer minAge, Integer maxAge){

    return customerOrderRepository.findProductsOrderedByCustomersFromSpecifiedCountryAndWithAgeWithinSpecifiedRange(countryName, minAge, maxAge)
            .stream().distinct().map(productMapper::productToProductDTO).sorted(Comparator.comparing(ProductDTO::getPrice).reversed()).collect(Collectors.toList());
  }

//  public List <CustomerOrderDto> get(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount){
//    return customerOrderRepository.findOrdersOrderedWithingSpecifiedDateRangeAndWithPriceAfterDiscountHigherThanSpecified(minDate,maxDate,minPriceAfterDiscount);
//  }
}
