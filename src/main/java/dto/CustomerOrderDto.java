package dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrderDto {

  private LocalDate date;
  private BigDecimal discount;
  private Integer quantity;
  private CustomerDto customer;
  private ProductDTO product;
  private PaymentDto payment;
}
