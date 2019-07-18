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


  List<Product> findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge);

  List<CustomerOrder> findProductsWithActiveWarranty();

  List<CustomerOrder> findProductsOrderedByCustomer(String customerName, String customerSurname, String countryName);

  List<CustomerOrder> findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount);
}
