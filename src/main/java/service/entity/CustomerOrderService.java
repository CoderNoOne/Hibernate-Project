package service.entity;


import domain.CustomerOrder;
import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import mapper.*;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.chooseAvailableProduct;
import static util.entity_utils.ShopUtil.chooseAvailableShop;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;
  private final StockService stockService;
  private final CustomerService customerService;
  private final ProductService productService;
  private final PaymentService paymentService;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
    this.stockService = new StockService();
    this.customerService = new CustomerService();
    this.productService = new ProductService();
    this.paymentService = new PaymentService();

  }

  public CustomerOrderService(CustomerOrderRepository customerOrderRepository, StockService stockService, CustomerService customerService, ProductService productService, PaymentService paymentService) {
    this.customerOrderRepository = customerOrderRepository;
    this.customerService = customerService;
    this.stockService = stockService;
    this.productService = productService;
    this.paymentService = paymentService;
  }

  private Optional<CustomerOrderDto> addCustomerOrderToDb(CustomerOrderDto customerOrderDto) {
    return customerOrderRepository
            .addOrUpdate(ModelMapper.mapCustomerOrderDtoToCustomerOrder(customerOrderDto))
            .map(ModelMapper::mapCustomerOrderToCustomerOrderDto);


  }

  private CustomerOrderDto setCustomerOrderComponentsFromDbIfTheyExist(CustomerOrderDto customerOrder) {

    return CustomerOrderDto.builder()
            .id(customerOrder.getId())
            .payment(paymentService.getPaymentFromDbIfExists(customerOrder.getPayment()))
            .discount(customerOrder.getDiscount())
            .date(customerOrder.getDate())
            .quantity(customerOrder.getQuantity())
            .product(productService.getProductFromDbIfExists(customerOrder.getProduct()))
            .customer(customerService.getCustomerDtoFromDbIfExists(customerOrder.getCustomer()))
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

    var productsByNameAndCategory = productService.getProductsByNameAndCategory(customerOrderFromUserInput.getProduct().getName(),
            customerOrderFromUserInput.getProduct().getCategoryDto());

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

    customerService.getCustomerByNameAndSurnameAndCountry(customerName,
            customerSurname, customerCountry)
            .ifPresentOrElse(
                    customerOrder::setCustomer,
                    () -> {
                      throw new AppException(String.format("There is no customer in a db with: name: %s surname: %s country: %s",
                              customerName, customerSurname, customerCountry.getName()));
                    });

    return customerOrder;
  }

  public Map<ShopDto, Integer> specifyShopDetailForCustomerOrder(CustomerOrderDto customerOrderDto) {

    Map<ShopDto, Integer> shopMap = stockService.getShopListWithProductInStock(customerOrderDto.getProduct());

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
