package repository.abstract_repository.entity;

import domain.*;
import domain.enums.EGuarantee;

import repository.abstract_repository.base.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Long> {

  Map<Category, Map<Product, Integer>> findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases();

  List<Product> findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge);

  List<Product> findProductsWithActiveWarrantyAndWithGuaranteeComponents(Set<EGuarantee> guaranteeSet);

  Map<Producer, List<Product>> findProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName);

  Map<Customer, Long> findCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim();

  Map<Product, Integer> findNumberOfProductsOrders(List<Product> productList);

  List<CustomerOrder> findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount);
}
