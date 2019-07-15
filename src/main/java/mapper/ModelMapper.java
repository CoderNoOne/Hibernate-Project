package mapper;

import domain.*;
import domain.Error;
import dto.*;

public interface ModelMapper {


  static Error mapErrorDtoToError(ErrorDto errorDto) {

    return Error.builder()
            .id(errorDto.getId())
            .message(errorDto.getMessage())
            .date(errorDto.getDate())
            .build();
  }

  static ErrorDto mapErrorToErrorDto(Error error) {

    return ErrorDto.builder()
            .id(error.getId())
            .message(error.getMessage())
            .date(error.getDate())
            .build();
  }

  static Trade mapTradeDtoToTrade(TradeDto tradeDto) {

    return Trade.builder()
            .id(tradeDto.getId())
            .name(tradeDto.getName())
            .build();
  }

  static TradeDto mapTradeToTradeDto(Trade trade) {

    return TradeDto.builder()
            .id(trade.getId())
            .name(trade.getName())
            .build();
  }

  static Payment mapPaymentDtoToPayment(PaymentDto paymentDto) {

    return Payment.builder()
            .id(paymentDto.getId())
            .epayment(paymentDto.getEpayment())
            .build();
  }

  static PaymentDto mapPaymentToPaymentDto(Payment payment) {
    return PaymentDto.builder()
            .id(payment.getId())
            .epayment(payment.getEpayment())
            .build();
  }

  static Category mapCategoryDtoToCategory(CategoryDto categoryDto) {
    return Category.builder()
            .id(categoryDto.getId())
            .name(categoryDto.getName())
            .build();
  }

  static CategoryDto mapCategoryToCategoryDto(Category category) {

    return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
  }

  static CountryDto mapCountryToCountryDto(Country country) {
    return CountryDto.builder()
            .id(country.getId())
            .name(country.getName())
            .build();
  }

  static Country mapCountryDtoToCountry(CountryDto countryDto) {
    return Country.builder()
            .id(countryDto.getId())
            .name(countryDto.getName())
            .build();
  }


  static CustomerDto mapCustomerToCustomerDto(Customer customer) {
    return CustomerDto.builder()
            .id(customer.getId())
            .age(customer.getAge())
            .name(customer.getName())
            .surname(customer.getSurname())
            .countryDto(mapCountryToCountryDto(customer.getCountry())
            ).build();
  }

  static Customer mapCustomerDtoToCustomer(CustomerDto customerDto) {

    return Customer.builder()
            .id(customerDto.getId())
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .age(customerDto.getAge())
            .country(mapCountryDtoToCountry(customerDto.getCountryDto()))
            .build();
  }

  static Producer mapProducerDtoToProducer(ProducerDto producerDto) {

    return Producer.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(mapTradeDtoToTrade(producerDto.getTrade()))
            .country(mapCountryDtoToCountry(producerDto.getCountry()))
            .build();
  }

  static ProducerDto mapProducerToProducerDto(Producer producer) {

    return ProducerDto.builder()
            .id(producer.getId())
            .name(producer.getName())
            .trade(mapTradeToTradeDto(producer.getTrade()))
            .country(mapCountryToCountryDto(producer.getCountry()))
            .build();
  }

  static Product mapProductDtoToProduct(ProductDto productDto) {

    return Product.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .category(mapCategoryDtoToCategory(productDto.getCategoryDto()))
            .producer(mapProducerDtoToProducer(productDto.getProducerDto()))
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .build();
  }

  static ProductDto mapProductToProductDto(Product product) {

    return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .categoryDto(mapCategoryToCategoryDto(product.getCategory()))
            .producerDto(mapProducerToProducerDto(product.getProducer()))
            .guaranteeComponents(product.getGuaranteeComponents())
            .build();
  }

  static ShopDto mapShopToShopDto(Shop shop) {
    return ShopDto.builder()
            .id(shop.getId())
            .name(shop.getName())
            .countryDto(mapCountryToCountryDto(shop.getCountry()))
            .build();
  }


  static Shop mapShopDtoToShop(ShopDto shopDto) {

    return Shop.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .country(mapCountryDtoToCountry(shopDto.getCountryDto()))
            .build();
  }

  static StockDto mapStockToStockDto(Stock stock) {

    return dto.StockDto.builder()
            .id(stock.getId())
            .quantity(stock.getQuantity())
            .productDto(mapProductToProductDto(stock.getProduct()))
            .shopDto(mapShopToShopDto(stock.getShop()))
            .build();
  }

  static Stock mapStockDtoToStock(StockDto stockDto) {

    return Stock.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .product(mapProductDtoToProduct(stockDto.getProductDto()))
            .shop(mapShopDtoToShop(stockDto.getShopDto()))
            .build();
  }


  static CustomerOrderDto mapCustomerOrderToCustomerOrderDto(CustomerOrder customerOrder) {

    return CustomerOrderDto.builder()
            .id(customerOrder.getId())
            .discount(customerOrder.getDiscount())
            .quantity(customerOrder.getQuantity())
            .customer(mapCustomerToCustomerDto(customerOrder.getCustomer()))
            .payment(mapPaymentToPaymentDto(customerOrder.getPayment()))
            .product(mapProductToProductDto(customerOrder.getProduct()))
            .date(customerOrder.getDate())
            .build();
  }

  static CustomerOrder mapCustomerOrderDtoToCustomerOrder(CustomerOrderDto customerOrderDto) {

    return CustomerOrder.builder()
            .id(customerOrderDto.getId())
            .discount(customerOrderDto.getDiscount())
            .quantity(customerOrderDto.getQuantity())
            .customer(mapCustomerDtoToCustomer(customerOrderDto.getCustomer()))
            .payment(mapPaymentDtoToPayment(customerOrderDto.getPayment()))
            .product(mapProductDtoToProduct(customerOrderDto.getProduct()))
            .date(customerOrderDto.getDate())
            .build();
  }
}
