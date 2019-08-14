package service.entity;


import domain.CustomerOrder;
import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;

  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final PaymentRepository paymentRepository;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();

    this.customerRepository = new CustomerRepositoryImpl();
    this.productRepository = new ProductRepositoryImpl();
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  private Optional<CustomerOrderDto> addCustomerOrderToDb(CustomerOrderDto customerOrderDto) {

    CustomerOrder customerOrder = ModelMapper.mapCustomerOrderDtoToCustomerOrder(customerOrderDto);

    customerRepository.findByNameAndSurnameAndCountry(customerOrder.getCustomer().getName(), customerOrder.getCustomer().getSurname(),
            customerOrder.getCustomer().getCountry()).ifPresent(customerOrder::setCustomer);

    paymentRepository.findPaymentByEPayment(customerOrder.getPayment().getEpayment())
            .ifPresentOrElse(customerOrder::setPayment, () -> paymentRepository.add(customerOrder.getPayment()));

    productRepository.findByNameAndCategoryAndProducer(customerOrder.getProduct().getName(), customerOrder.getProduct().getCategory(),
            customerOrder.getProduct().getProducer()).ifPresent(customerOrder::setProduct);

    customerOrderRepository.add(customerOrder);

    System.out.println(customerOrder);

    return Optional.of(customerOrder).map(ModelMapper::mapCustomerOrderToCustomerOrderDto);

  }

  private CustomerOrderDto setCustomerOrderComponentsFromDbIfTheyExist(CustomerOrderDto customerOrder) {

    return CustomerOrderDto.builder()
            .id(customerOrder.getId())
            .payment(paymentRepository.findPaymentByEPayment(customerOrder.getPayment().getEpayment()).map(ModelMapper::mapPaymentToPaymentDto).orElse(customerOrder.getPayment()))
            .discount(customerOrder.getDiscount())
            .date(customerOrder.getDate())
            .quantity(customerOrder.getQuantity())
            .product(productRepository.findByNameAndCategoryAndProducer(customerOrder.getProduct().getName(),
                    ModelMapper.mapCategoryDtoToCategory(customerOrder.getProduct().getCategoryDto()),
                    ModelMapper.mapProducerDtoToProducer(customerOrder.getProduct().getProducerDto()))
                    .map(ModelMapper::mapProductToProductDto).orElse(customerOrder.getProduct()))
            .customer(customerRepository.findByNameAndSurnameAndCountry(customerOrder.getCustomer().getName(),
                    customerOrder.getCustomer().getSurname(), ModelMapper.mapCountryDtoToCountry(customerOrder.getCustomer().getCountryDto()))
                    .map(ModelMapper::mapCustomerToCustomerDto).orElse(customerOrder.getCustomer()))
            .build();
  }

  public void addCustomerOrderToDbFromUserInput(CustomerOrderDto customerOrderDto) {

    addCustomerOrderToDb(/*setCustomerOrderComponentsFromDbIfTheyExist*/(customerOrderDto));
  }

  public Map<CategoryDto, Map<ProductDto, Integer>> getTheMostExpensiveProductsInEachCategoryWithAmountOfProductSales() {

    List<CustomerOrderDto> customerOrderDtoList = getAllCustomerOrders();

    return customerOrderDtoList.stream().map(customerOrder -> customerOrder.getProduct().getCategoryDto()).distinct()
            .collect(Collectors.toMap(
                    Function.identity(),
                    category -> {
                      Map<CategoryDto, Integer> theHighestQuantityOrderInEachCategory = customerOrderDtoList.stream().filter(customerOrder -> customerOrder.getProduct().getCategoryDto().equals(category))
                              .collect(Collectors.groupingBy(customerOrder -> customerOrder.getProduct().getCategoryDto(),
                                      Collectors.mapping(CustomerOrderDto::getQuantity, Collectors.reducing(0, (v1, v2) -> v1 >= v2 ? v1 : v2))));

                      return customerOrderDtoList.stream().filter(customerOrderDto -> customerOrderDto.getProduct().getCategoryDto().equals(category))
                              .collect(Collectors.groupingBy(CustomerOrderDto::getProduct,
                                      Collectors.mapping(CustomerOrderDto::getQuantity, Collectors.filtering(
                                              quantity -> quantity.intValue() == theHighestQuantityOrderInEachCategory.get(category).intValue(),
                                              Collectors.reducing(0, (v1, v2) -> v1 > v2 ? v1 : v2)))));
                    }));
  }


  public List<ProductDto> getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinRangeAndSortedByPriceDescOrder(String countryName, Integer minAge, Integer maxAge) {

    if (countryName == null || minAge == null || maxAge == null) {
      throw new AppException(String.format("At least one argument is null (countryName: %s minAge %d maxAge: %d", countryName, minAge, maxAge));
    }

    if (minAge > maxAge) {
      throw new AppException(String.format("Min age: %d is greater than %d", minAge, maxAge));
    }
    return customerOrderRepository.findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(countryName, minAge, maxAge)
            .stream()
            .distinct()
            .map(ModelMapper::mapProductToProductDto)
            .sorted(Comparator.comparing(ProductDto::getPrice).reversed())
            .collect(Collectors.toList());
  }

  public List<CustomerOrderDto> getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThan(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {

    if (minDate == null || maxDate == null || minPriceAfterDiscount == null) {
      throw new AppException(String.format("At least one of the method arguments's not valid: minDate:%s maxDate: %s minPriceAfterDiscount: %s", minDate, maxDate, minPriceAfterDiscount));
    }

    if (minDate.compareTo(maxDate) > 0) {
      throw new AppException("minDate: " + minDate + " is after maxDate: " + maxDate);
    }
    return customerOrderRepository.
            findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount)
            .stream()
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }

  public Map<String, List<ProductDto>> getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(Set<EGuarantee> guaranteeComponents) {

    if (guaranteeComponents == null) {
      throw new AppException("guaranteeComponents collection object is null");
    }
    return customerOrderRepository.findProductsWithActiveWarranty()
            .stream()
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto)
            .filter(customerOrder -> guaranteeComponents.isEmpty() || customerOrder.getProduct().getGuaranteeComponents().stream().anyMatch(guaranteeComponents::contains))
            .map(CustomerOrderDto::getProduct)
            .collect(Collectors.groupingBy(productDto -> productDto.getCategoryDto().getName()));
  }


  public Map<ProducerDto, List<ProductDto>> getProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName) {

    if (customerName == null || customerSurname == null || countryName == null) {
      throw new AppException("getProductsOrderedByCustomerGroupedByProducer - not valid input data");
    }

    return customerOrderRepository.findProductsOrderedByCustomer(customerName, customerSurname, countryName)
            .stream()
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto)
            .collect(Collectors.groupingBy(c ->c.getProduct().getProducerDto(),
                    Collectors.mapping(CustomerOrderDto::getProduct, Collectors.toList())));
  }


  public Map<CustomerDto, Long> getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim() {

    List<CustomerOrderDto> allCustomerOrders = getAllCustomerOrders();

    return allCustomerOrders.stream().map(CustomerOrderDto::getCustomer).distinct()
            .collect(Collectors.collectingAndThen(Collectors.toMap(
                    Function.identity(),
                    customer -> (Long) allCustomerOrders.stream().filter(customerOrder -> customerOrder.getCustomer().equals(customer))
                            .filter(customerOrder -> customerOrder.getProduct().getProducerDto().getCountry().equals(customer.getCountryDto())).map(CustomerOrderDto::getQuantity).count()),
                    map -> map.entrySet().stream().filter(e -> e.getValue() >= 1).map(Map.Entry::getKey).collect(Collectors.toMap(
                            Function.identity(),
                            customer -> (Long) allCustomerOrders.stream().filter(customerOrder -> customerOrder.getCustomer().equals(customer))
                                    .filter(customerOrder -> !customerOrder.getProduct().getProducerDto().getCountry().equals(customer.getCountryDto())).map(CustomerOrderDto::getQuantity).count()))));

  }

  public void deleteAllCustomerOrders() {
    customerOrderRepository.deleteAll();
  }

  public List<CustomerOrderDto> getAllCustomerOrders() {

    return customerOrderRepository.findAll()
            .stream()
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }
}
