package dto;

import domain.Category;
import domain.Producer;
import domain.enums.EGuarantee;
import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {

  private String name;
  private BigDecimal price;
  private Category category;
  private Producer producer;
  private List<EGuarantee> guaranteeComponent;
}
