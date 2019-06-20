package service;

import domain.CustomerOrder;
import domain.Product;
import domain.enums.EGuarantee;
import dto.CategoryDto;
import dto.CustomerOrderDto;
import dto.ProductDto;
import mapper.CategoryMapper;
import mapper.CustomerOrderMapper;
import mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;
  private final CategoryMapper categoryMapper;
  private final ProductMapper productMapper;
  private final CustomerOrderMapper customerOrderMapper;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
    this.categoryMapper = Mappers.getMapper(CategoryMapper.class);
    this.productMapper = Mappers.getMapper(ProductMapper.class);
    this.customerOrderMapper = Mappers.getMapper(CustomerOrderMapper.class);
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

  public Map<CategoryDto, Map<ProductDto, Integer>> getTheMostExpensiveProductsInEachCategoryWithAmountOfProductSales() {

    return customerOrderRepository.findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases()
            .entrySet().stream().collect(Collectors.toMap(
                    e -> categoryMapper.categoryToCategoryDTO(e.getKey()),
                    e -> e.getValue().entrySet().stream().collect(Collectors.toMap(
                            ee -> productMapper.productToProductDTO(ee.getKey()),
                            Map.Entry::getValue
                    ))));
  }

  public List<ProductDto> getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinSpecifiedRangeAndSortedByPriceDescOrder(String countryName, Integer minAge, Integer maxAge) {

    return customerOrderRepository.findProductsOrderedByCustomersFromSpecifiedCountryAndWithAgeWithinSpecifiedRange(countryName, minAge, maxAge)
            .stream().distinct().map(productMapper::productToProductDTO).sorted(Comparator.comparing(ProductDto::getPrice).reversed()).collect(Collectors.toList());
  }

  public List<CustomerOrderDto> getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDicountHigherThanSpecified(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {
    return customerOrderRepository.
            findOrdersOrderedWithingSpecifiedDateRangeAndWithPriceAfterDiscountHigherThanSpecified(minDate, maxDate, minPriceAfterDiscount)
            .stream()
            .map(customerOrderMapper::customerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }

  public Map<String, List<ProductDto>> getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(Set<EGuarantee> guaranteeComponents) {

    return customerOrderRepository.findProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponents(guaranteeComponents)
            .stream().map(productMapper::productToProductDTO).collect(Collectors.groupingBy(ProductDto::getCategoryName));
  }
}
