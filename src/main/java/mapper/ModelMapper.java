package mapper;

import domain.*;
import domain.Error;
import dto.*;

import java.util.stream.Collectors;

public interface ModelMapper {

  static Error mapErrorDtoToError(ErrorDto errorDto) {

    return errorDto != null ? Error.builder()
            .id(errorDto.getId())
            .message(errorDto.getMessage())
            .date(errorDto.getDate())
            .build() : null;
  }

  static ErrorDto mapErrorToErrorDto(Error error) {

    return error != null ? ErrorDto.builder()
            .id(error.getId())
            .message(error.getMessage())
            .date(error.getDate())
            .build() : null;
  }

  static Trade mapTradeDtoToTrade(TradeDto tradeDto) {

    return tradeDto != null ? Trade.builder()
            .id(tradeDto.getId())
            .name(tradeDto.getName())
            .build() : null;
  }

  static TradeDto mapTradeToTradeDto(Trade trade) {

    return trade != null ? TradeDto.builder()
            .id(trade.getId())
            .name(trade.getName())
            .build() : null;
  }

  static Payment mapPaymentDtoToPayment(PaymentDto paymentDto) {

    return paymentDto != null ? Payment.builder()
            .id(paymentDto.getId())
            .epayment(paymentDto.getEpayment())
            .build() : null;
  }

  static PaymentDto mapPaymentToPaymentDto(Payment payment) {
    return payment != null ? PaymentDto.builder()
            .id(payment.getId())
            .epayment(payment.getEpayment())
            .build() : null;
  }

  static Category mapCategoryDtoToCategory(CategoryDto categoryDto) {
    return categoryDto != null ? Category.builder()
            .id(categoryDto.getId())
            .name(categoryDto.getName())
            .build() : null;
  }

  static CategoryDto mapCategoryToCategoryDto(Category category) {

    return category != null ? CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build() : null;
  }

  static CountryDto mapCountryToCountryDto(Country country) {
    return country != null ? CountryDto.builder()
            .id(country.getId())
            .name(country.getName())
            .build() : null;
  }

  static Country mapCountryDtoToCountry(CountryDto countryDto) {
    return countryDto != null ? Country.builder()
            .id(countryDto.getId())
            .name(countryDto.getName())
            .build() : null;
  }


  static CustomerDto mapCustomerToCustomerDto(Customer customer) {
    return customer != null ? CustomerDto.builder()
            .id(customer.getId())
            .age(customer.getAge())
            .name(customer.getName())
            .surname(customer.getSurname())
            .countryDto(mapCountryToCountryDto(customer.getCountry())
            ).build() : null;
  }

  static Customer mapCustomerDtoToCustomer(CustomerDto customerDto) {

    return customerDto != null ? Customer.builder()
            .id(customerDto.getId())
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .age(customerDto.getAge())
            .country(mapCountryDtoToCountry(customerDto.getCountryDto()))
            .build() : null;
  }

  static Producer mapProducerDtoToProducer(ProducerDto producerDto) {

    return producerDto != null ? Producer.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(mapTradeDtoToTrade(producerDto.getTrade()))
            .country(mapCountryDtoToCountry(producerDto.getCountry()))
            .build() : null;
  }

  static ProducerDto mapProducerToProducerDto(Producer producer) {

    return producer != null ? ProducerDto.builder()
            .id(producer.getId())
            .name(producer.getName())
            .trade(mapTradeToTradeDto(producer.getTrade()))
            .country(mapCountryToCountryDto(producer.getCountry()))
            .build() : null;
  }

  static Product mapProductDtoToProduct(ProductDto productDto) {

    return productDto != null ? Product.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .category(mapCategoryDtoToCategory(productDto.getCategoryDto()))
            .producer(mapProducerDtoToProducer(productDto.getProducerDto()))
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .build() : null;
  }

  static ProductDto mapProductToProductDto(Product product) {

    return product != null ? ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .categoryDto(mapCategoryToCategoryDto(product.getCategory()))
            .producerDto(mapProducerToProducerDto(product.getProducer()))
            /*guaranteeComponents list has to be recollect (if not, strange behaviour happens when comparison between productDto objects is performed*/
            .guaranteeComponents(product.getGuaranteeComponents() != null ? product.getGuaranteeComponents().stream().collect(Collectors.toList()) : null)
            .build() : null;
  }

  static ShopDto mapShopToShopDto(Shop shop) {
    return shop != null ? ShopDto.builder()
            .id(shop.getId())
            .name(shop.getName())
            .countryDto(mapCountryToCountryDto(shop.getCountry()))
            .build() : null;
  }


  static Shop mapShopDtoToShop(ShopDto shopDto) {

    return shopDto != null ? Shop.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .country(mapCountryDtoToCountry(shopDto.getCountryDto()))
            .build() : null;
  }

  static StockDto mapStockToStockDto(Stock stock) {

    return stock != null ? StockDto.builder()
            .id(stock.getId())
            .quantity(stock.getQuantity())
            .productDto(mapProductToProductDto(stock.getProduct()))
            .shopDto(mapShopToShopDto(stock.getShop()))
            .build() : null;
  }

  static Stock mapStockDtoToStock(StockDto stockDto) {

    return stockDto != null ? Stock.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .product(mapProductDtoToProduct(stockDto.getProductDto()))
            .shop(mapShopDtoToShop(stockDto.getShopDto()))
            .build() : null;
  }


  static CustomerOrderDto mapCustomerOrderToCustomerOrderDto(CustomerOrder customerOrder) {

    return customerOrder != null ? CustomerOrderDto.builder()
            .id(customerOrder.getId())
            .discount(customerOrder.getDiscount())
            .quantity(customerOrder.getQuantity())
            .customer(mapCustomerToCustomerDto(customerOrder.getCustomer()))
            .payment(mapPaymentToPaymentDto(customerOrder.getPayment()))
            .product(mapProductToProductDto(customerOrder.getProduct()))
            .date(customerOrder.getDate())
            .build() : null;

  }

  static CustomerOrder mapCustomerOrderDtoToCustomerOrder(CustomerOrderDto customerOrderDto) {

    return customerOrderDto != null ? CustomerOrder.builder()
            .id(customerOrderDto.getId())
            .discount(customerOrderDto.getDiscount())
            .quantity(customerOrderDto.getQuantity())
            .customer(mapCustomerDtoToCustomer(customerOrderDto.getCustomer()))
            .payment(mapPaymentDtoToPayment(customerOrderDto.getPayment()))
            .product(mapProductDtoToProduct(customerOrderDto.getProduct()))
            .date(customerOrderDto.getDate())
            .build() : null;
  }
}
