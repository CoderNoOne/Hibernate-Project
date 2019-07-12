package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

  private Long id;
  private Integer quantity;
  private ShopDto shopDto;
  private ProductDto productDto;
}
