package dto;

import domain.enums.EGuarantee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductDto {

  private Long id;
  private String name;
  private BigDecimal price;
  private CategoryDto categoryDto;
  private ProducerDto producerDto;
  private List<EGuarantee> guaranteeComponents = new ArrayList<>();

}
