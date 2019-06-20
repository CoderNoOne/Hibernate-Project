package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductDto {

  private String productName;
  private BigDecimal price;
  private String categoryName;
  private String producerName;
  private String countryName;

}
