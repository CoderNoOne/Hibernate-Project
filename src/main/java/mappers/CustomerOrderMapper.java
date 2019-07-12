package mappers;

import domain.*;
import dto.*;

public class CustomerOrderMapper {

  public CustomerOrder mapCustomerOrderDtoToCusomerOrder(CustomerOrderDto customerOrderDto) {

    return CustomerOrder.builder()
            .id(customerOrderDto.getId())
            .discount(customerOrderDto.getDiscount())
            .quantity(customerOrderDto.getQuantity())
            .customer(Customer.builder()
                    .id(customerOrderDto.getCustomer().getId())
                    .age(customerOrderDto.getCustomer().getAge())
                    .build())
            .payment(Payment.builder()
                    .id(customerOrderDto.getPayment().getId())
                    .epayment(customerOrderDto.getPayment().getEpayment())
                    .build())
            .product(Product.builder()
                    .id(customerOrderDto.getProduct().getId())
                    .name(customerOrderDto.getProduct().getName())
                    .producer(Producer.builder()
                            .id(customerOrderDto.getProduct().getProducerDto().getId())
                            .name(customerOrderDto.getProduct().getProducerDto().getName())
                            .country(Country.builder()
                                    .id(customerOrderDto.getProduct().getProducerDto().getCountry().getId())
                                    .name(customerOrderDto.getProduct().getProducerDto().getCountry().getName())
                                    .build())
                            .trade(Trade.builder()
                                    .id(customerOrderDto.getProduct().getProducerDto().getTrade().getId())
                                    .name(customerOrderDto.getProduct().getProducerDto().getTrade().getName())
                                    .build())
                            .build())
                    .guaranteeComponents(customerOrderDto.getProduct().getGuaranteeComponents())
                    .category(Category.builder()
                            .id(customerOrderDto.getProduct().getCategoryDto().getId())
                            .name(customerOrderDto.getProduct().getCategoryDto().getName())
                            .build())
                    .build())
            .date(customerOrderDto.getDate())
            .build();
  }

  public CustomerOrderDto mapCustomerOrderToCustomerOrderDto(CustomerOrder customerOrder) {

    return CustomerOrderDto.builder()
            .id(customerOrder.getId())
            .discount(customerOrder.getDiscount())
            .quantity(customerOrder.getQuantity())
            .customer(CustomerDto.builder()
                    .id(customerOrder.getCustomer().getId())
                    .age(customerOrder.getCustomer().getAge())
                    .build())
            .payment(PaymentDto.builder()
                    .id(customerOrder.getPayment().getId())
                    .epayment(customerOrder.getPayment().getEpayment())
                    .build())
            .product(ProductDto.builder()
                    .id(customerOrder.getProduct().getId())
                    .name(customerOrder.getProduct().getName())
                    .producerDto(ProducerDto.builder()
                            .id(customerOrder.getProduct().getProducer().getId())
                            .name(customerOrder.getProduct().getProducer().getName())
                            .country(CountryDto.builder()
                                    .id(customerOrder.getProduct().getProducer().getCountry().getId())
                                    .name(customerOrder.getProduct().getProducer().getCountry().getName())
                                    .build())
                            .trade(TradeDto.builder()
                                    .id(customerOrder.getProduct().getProducer().getTrade().getId())
                                    .name(customerOrder.getProduct().getProducer().getTrade().getName())
                                    .build())
                            .build())
                    .guaranteeComponents(customerOrder.getProduct().getGuaranteeComponents())
                    .categoryDto(CategoryDto.builder()
                            .id(customerOrder.getProduct().getCategory().getId())
                            .name(customerOrder.getProduct().getCategory().getName())
                            .build())
                    .build())
            .date(customerOrder.getDate())
            .build();


  }
}
