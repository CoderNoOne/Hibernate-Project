package mapper;

import domain.Product;
import dto.ProductDto;
import org.mapstruct.*;


@Mapper
public interface ProductMapper {

  @Mappings({
          @Mapping(source = "name", target = "productName"),
          @Mapping(source = "category.name", target = "categoryName"),
          @Mapping(source = "producer.name", target = "producerName"),
          @Mapping(source = "producer.country.name", target = "countryName"),

  })
  ProductDto productToProductDTO(Product product);


  @Mappings({
          @Mapping(source = "productName", target = "name"),
          @Mapping(source = "categoryName", target = "category.name"),
          @Mapping(source = "producerName", target = "producer.name"),
          @Mapping(source = "countryName", target = "producer.country.name")
  })
  Product productDTOtoProduct(ProductDto productDTO);
}
