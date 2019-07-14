package service.entity;


import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import mappers.*;
import repository.abstract_repository.entity.CustomerOrderRepository;
import repository.impl.CustomerOrderRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.chooseAvailableProduct;
import static util.entity_utils.ShopUtil.chooseAvailableShop;

public class CustomerOrderService {

  private final CustomerOrderRepository customerOrderRepository;
  private final CategoryMapper categoryMapper;
  private final ProductMapper productMapper;
  private final CustomerOrderMapper customerOrderMapper;
  private final ProducerMapper producerMapper;
  private final CustomerMapper customerMapper;
  private final StockService stockService;
  private final CustomerService customerService;
  private final ProductService productService;
  private final PaymentService paymentService;

  public CustomerOrderService() {
    this.customerOrderRepository = new CustomerOrderRepositoryImpl();
    this.categoryMapper = new CategoryMapper();
    this.productMapper = new ProductMapper();
    this.customerOrderMapper = new CustomerOrderMapper();
    this.producerMapper = new ProducerMapper();
    this.customerMapper = new CustomerMapper();
    this.stockService = new StockService();
    this.customerService = new CustomerService();
    this.productService = new ProductService();
    this.paymentService = new PaymentService();
  }

  private Optional<CustomerOrderDto> addCustomerOrderToDb(CustomerOrderDto customerOrderDto) {
    return customerOrderRepository
            .addOrUpdate(customerOrderMapper.mapCustomerOrderDtoToCustomerOrder(customerOrderDto))
            .map(customerOrderMapper::mapCustomerOrderToCustomerOrderDto);


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

    return customerOrderRepository.findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases()
            .entrySet().stream().collect(Collectors.toMap(
                    e -> categoryMapper.mapCategoryToCategoryDto(e.getKey()),
                    e -> e.getValue().entrySet().stream().collect(Collectors.toMap(
                            ee -> productMapper.mapProductToProductDto(ee.getKey()),
                            Map.Entry::getValue
                    ))));
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
            .stream().distinct().map(productMapper::mapProductToProductDto).sorted(Comparator.comparing(ProductDto::getPrice).reversed()).collect(Collectors.toList());
  }

  public List<dto.CustomerOrderDto> getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDicountHigherThanSpecified(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {
    return customerOrderRepository.
            findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(minDate, maxDate, minPriceAfterDiscount)
            .stream()
            .map(customerOrderMapper::mapCustomerOrderToCustomerOrderDto)
            .collect(Collectors.toList());
  }

  public Map<String, List<ProductDto>> getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(Set<EGuarantee> guaranteeComponents) {

    return customerOrderRepository.findProductsWithActiveWarrantyAndWithGuaranteeComponents(guaranteeComponents)
            .stream().map(productMapper::mapProductToProductDto).collect(Collectors.groupingBy(ProductDto::getName));
  }

  public Map<ProducerDto, List<ProductDto>> getProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName) {

    return customerOrderRepository.findProductsOrderedByCustomerGroupedByProducer(customerName, customerSurname, countryName)
            .entrySet().stream().collect(Collectors.toMap(
                    e -> producerMapper.mapProducerToProducerDto(e.getKey()),
                    e -> e.getValue().stream().map(productMapper::mapProductToProductDto).collect(Collectors.toList())));
  }

  public Map<CustomerDto, Long> getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim() {

    return customerOrderRepository
            .findCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim()
            .entrySet().stream()
            .collect(Collectors.toMap(
                    e -> customerMapper.mapCustomerToCustomerDto(e.getKey()),
                    Map.Entry::getValue));
  }

  public void deleteAllCustomerOrders() {
    customerOrderRepository.deleteAll();
  }
}
