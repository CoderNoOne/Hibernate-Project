package service;

import domain.CustomerOrder;
import domain.Product;
import domain.enums.EGuarantee;
import dto.*;
import mapper.*;
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
  private final ProducerMapper producerMapper;
  private final CustomerMapper customerMapper;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
    this.categoryMapper = Mappers.getMapper(CategoryMapper.class);
    this.productMapper = Mappers.getMapper(ProductMapper.class);
    this.customerOrderMapper = Mappers.getMapper(CustomerOrderMapper.class);
    this.producerMapper = Mappers.getMapper(ProducerMapper.class);
    this.customerMapper = Mappers.getMapper(CustomerMapper.class);
  }

  private Optional<CustomerOrder> addCustomerOrderToDb(CustomerOrder customerOrder) {
    return customerOrderRepository.addOrUpdate(customerOrder);
  }

  public void addCustomerOrderToDbFromUserInput(CustomerOrder customerOrder) {
    addCustomerOrderToDb(customerOrder);
  }

  public Map<Product, Integer> getNumberOfOrdersForSpecifiedProducts(List<Product> productList) {
    return customerOrderRepository.findNumberOfProductsOrders(productList);
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

    return customerOrderRepository.findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(countryName, minAge, maxAge)
            .stream().distinct().map(productMapper::productToProductDTO).sorted(Comparator.comparing(ProductDto::getPrice).reversed()).collect(Collectors.toList());
  }

  public List<CustomerOrderDto> getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDicountHigherThanSpecified(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {
    return customerOrderRepository.
            findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount)
            .stream()
            .map(customerOrderMapper::customerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }

  public Map<String, List<ProductDto>> getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(Set<EGuarantee> guaranteeComponents) {

    return customerOrderRepository.findProductsWithActiveWarrantyAndWithGuaranteeComponents(guaranteeComponents)
            .stream().map(productMapper::productToProductDTO).collect(Collectors.groupingBy(ProductDto::getCategoryName));
  }

  public Map<ProducerDto, List<ProductDto>> getProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName) {

    return customerOrderRepository.findProductsOrderedByCustomerGroupedByProducer(customerName, customerSurname, countryName)
            .entrySet().stream().collect(Collectors.toMap(
                    e -> producerMapper.producerToProducerDto(e.getKey()),
                    e -> e.getValue().stream().map(productMapper::productToProductDTO).collect(Collectors.toList())));
  }

  public Map<CustomerDto, Long> getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim() {

    return customerOrderRepository
            .findCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim()
            .entrySet().stream()
            .collect(Collectors.toMap(
                    e -> customerMapper.customerToCustomerDto(e.getKey()),
                    Map.Entry::getValue));
  }

}
