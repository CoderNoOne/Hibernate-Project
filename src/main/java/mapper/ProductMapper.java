package mapper;

import domain.Category;
import domain.Product;
import dto.CategoryDTO;
import dto.ProductDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

  @Mappings({
          @Mapping(source = "name", target = "productName"),
          @Mapping(source = "category.name", target = "categoryName"),
          @Mapping(source = "producer.name", target = "producerName"),
          @Mapping(source = "producer.country.name", target = "countryName"),

  })
  ProductDTO productToProductDTO(Product product);


  @Mappings({
          @Mapping(source = "productName", target = "name"),
          @Mapping(source = "categoryName", target = "category.name"),
          @Mapping(source = "producerName", target = "producer.name"),
          @Mapping(source = "countryName", target = "producer.country.name")
  })
  Product productDTOtoProduct(ProductDTO productDTO);


  @MapMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  Map<CategoryDTO, List<ProductDTO>> productMapToProductDTOMap(Map<Category, List<Product>> map);
}
