package service.entity;


import domain.CustomerOrder;
import domain.Stock;
import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import mapper.*;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.chooseAvailableProduct;
import static util.entity_utils.ShopUtil.chooseAvailableShop;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;

  private final StockRepository stockRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final PaymentRepository paymentRepository;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();

    this.stockRepository = new StockRepositoryImpl();
    this.customerRepository = new CustomerRepositoryImpl();
    this.productRepository = new ProductRepositoryImpl();
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  public CustomerOrderService(CustomerOrderRepository customerOrderRepository, StockRepository stockRepository, CustomerRepository customerRepository, ProductRepository productRepository, PaymentRepository paymentRepository) {

    this.customerOrderRepository = customerOrderRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;
    this.paymentRepository = paymentRepository;
    this.stockRepository = stockRepository;
  }

  private Optional<CustomerOrderDto> addCustomerOrderToDb(CustomerOrderDto customerOrderDto) {
    return customerOrderRepository
            .addOrUpdate(ModelMapper.mapCustomerOrderDtoToCustomerOrder(customerOrderDto))
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto);

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

  public void addCustomerOrderToDbFromUserInput(CustomerOrderDto customerOrder) {
    addCustomerOrderToDb(setCustomerOrderComponentsFromDbIfTheyExist(customerOrder));
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


  public CustomerOrderDto specifyOrderedProductDetail(CustomerOrderDto customerOrderFromUserInput) {

    var productsByNameAndCategory = productRepository.findProductsByNameAndCategory(customerOrderFromUserInput.getProduct().getName(), ModelMapper.mapCategoryDtoToCategory(customerOrderFromUserInput.getProduct().getCategoryDto()))
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());

    if (!productsByNameAndCategory.isEmpty()) {
      customerOrderFromUserInput.setProduct(chooseAvailableProduct(productsByNameAndCategory));
      return customerOrderFromUserInput;
    }

    throw new AppException(String.format("There wasn't any product in a DB for product name: %s and product category: %s",
            customerOrderFromUserInput.getProduct().getName(), customerOrderFromUserInput.getProduct().getCategoryDto().getName()));
  }

  public CustomerOrderDto specifyCustomerDetail(CustomerOrderDto customerOrder) {

    if (customerOrder == null) {
      throw new AppException("CustomerOrder object is null");
    }

    if (customerOrder.getCustomer() == null) {
      throw new AppException("Customer object is null");
    }

    var customerName = customerOrder.getCustomer().getName();
    var customerSurname = customerOrder.getCustomer().getSurname();
    var customerCountry = customerOrder.getCustomer().getCountryDto();

    customerRepository.findByNameAndSurnameAndCountry(customerName, customerSurname, ModelMapper.mapCountryDtoToCountry(customerCountry))
            .map(ModelMapper::mapCustomerToCustomerDto)
            .ifPresentOrElse(
                    customerOrder::setCustomer,
                    () -> {
                      throw new AppException(String.format("There is no customer in a db with: name: %s surname: %s country: %s",
                              customerName, customerSurname, customerCountry.getName()));
                    });

    return customerOrder;
  }

  public Map<ShopDto, Integer> specifyShopDetailForCustomerOrder(CustomerOrderDto customerOrderDto) {

    Map<ShopDto, Integer> shopMap = stockRepository.findShopsWithProductInStock(ModelMapper.mapProductDtoToProduct(customerOrderDto.getProduct()))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                    e -> ModelMapper.mapShopToShopDto(e.getKey()),
                    Map.Entry::getValue
            ));

    if (!shopMap.isEmpty()) {
      var shop = chooseAvailableShop(new ArrayList<>(shopMap.keySet()));

      return Collections.singletonMap(shop, shopMap.get(shop));
    }

    throw new AppException("Product of interest isn't for sale in any of the registered shops in a DB");
  }

  public List<ProductDto> getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinSpecifiedRangeAndSortedByPriceDescOrder(String countryName, Integer minAge, Integer maxAge) {

    return customerOrderRepository.findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(countryName, minAge, maxAge)
            .stream().distinct().map(ModelMapper::mapProductToProductDto).sorted(Comparator.comparing(ProductDto::getPrice).reversed()).collect(Collectors.toList());
  }

  public List<dto.CustomerOrderDto> getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThanSpecified(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {
    return customerOrderRepository.
            findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount)
            .stream()
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }

  public Map<String, List<ProductDto>> getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(Set<EGuarantee> guaranteeComponents) {

    return customerOrderRepository.findProductsWithActiveWarranty()
            .stream().filter(customerOrder -> guaranteeComponents.isEmpty() || customerOrder.getProduct().getGuaranteeComponents().stream().anyMatch(guaranteeComponents::contains))
            .map(customerOrder -> ModelMapper.mapProductToProductDto(customerOrder.getProduct()))
            .collect(Collectors.groupingBy(ProductDto::getName));
  }


  public Map<ProducerDto, List<ProductDto>> getProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName) {

    if (customerName == null || customerSurname == null || countryName == null) {
      throw new AppException("getProductsOrderedByCustomerGroupedByProducer - not valid input data");
    }

    return customerOrderRepository.findProductsOrderedByCustomer(customerName, customerSurname, countryName)
            .stream().collect(Collectors.groupingBy(customerOrder -> customerOrder.getProduct().getProducer(),
                    Collectors.mapping(CustomerOrder::getProduct, Collectors.toList())))
            .entrySet().stream().collect(Collectors.toMap(
                    e -> ModelMapper.mapProducerToProducerDto(e.getKey()),
                    e -> e.getValue().stream().map(ModelMapper::mapProductToProductDto).collect(Collectors.toList())));
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
