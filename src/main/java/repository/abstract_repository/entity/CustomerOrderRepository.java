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

  Map<Product, Integer> findNumberOfProductsOrders(List<Product> productList);

  //Pobranie z bazy danych pełnej informacji na temat produktów o największej cenie w każdej kategorii.
  Map<Category, Map<Product, Integer>> findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases();

  //Pobranie z bazy danych listy wszystkich produktów, które zamawiane były przez klientów pochodzących
  // z kraju o nazwie podanej przez użytkownika i wieku z przedziału określanego przez użytkownika.

  List<Product> findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge);

  //Pobranie z bazy danych zamówień, które złożono w przedziale dat
  // pobranym od użytkownika o kwocie zamówienia (po uwzględnieniu zniżki) większej niż wartość podana przez użytkownika.

  List<CustomerOrder> findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount);

  List<Product> findProductsWithActiveWarrantyAndWithGuaranteeComponents(Set<EGuarantee> guaranteeSet);

  Map<Producer, List<Product>> findProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName);

  Map<Customer, Long> findCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim();
}
