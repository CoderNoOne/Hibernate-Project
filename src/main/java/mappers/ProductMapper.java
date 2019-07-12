package mappers;

import domain.*;
import dto.*;

public class ProductMapper {

  public Product mapProductDtoToProduct(ProductDto productDto) {

    return Product.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .category(Category.builder()
                    .id(productDto.getCategoryDto().getId())
                    .name(productDto.getCategoryDto().getName())
                    .build())
            .producer(Producer.builder()
                    .id(productDto.getProducerDto().getId())
                    .name(productDto.getProducerDto().getName())
                    .country(Country.builder()
                            .id(productDto.getProducerDto().getCountry().getId())
                            .name(productDto.getProducerDto().getCountry().getName())
                            .build())
                    .trade(Trade.builder()
                            .id(productDto.getProducerDto().getTrade().getId())
                            .name(productDto.getProducerDto().getTrade().getName())
                            .build())
                    .build())
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .build();
  }

  public ProductDto mapProductToProductDto(Product product) {

    return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .categoryDto(CategoryDto.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build())
            .producerDto(ProducerDto.builder()
                    .id(product.getProducer().getId())
                    .name(product.getProducer().getName())
                    .country(CountryDto.builder()
                            .id(product.getProducer().getCountry().getId())
                            .name(product.getProducer().getCountry().getName())
                            .build())
                    .trade(TradeDto.builder()
                            .id(product.getProducer().getTrade().getId())
                            .name(product.getProducer().getTrade().getName())
                            .build())
                    .build())
            .guaranteeComponents(product.getGuaranteeComponents())
            .build();
  }
}
