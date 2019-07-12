package mappers;

import domain.*;
import domain.Stock;
import dto.*;

public class StockMapper {

  public dto.StockDto mapStockToStockDto(Stock stock) {

    return dto.StockDto.builder()
            .id(stock.getId())
            .quantity(stock.getQuantity())
            .productDto(ProductDto.builder()
                    .id(stock.getProduct().getId())
                    .categoryDto(CategoryDto.builder()
                            .id(stock.getProduct().getCategory().getId())
                            .name(stock.getProduct().getCategory().getName())
                            .build())
                    .producerDto(ProducerDto.builder()
                            .id(stock.getProduct().getProducer().getId())
                            .name(stock.getProduct().getProducer().getName())
                            .country(CountryDto.builder()
                                    .id(stock.getProduct().getProducer().getCountry().getId())
                                    .name(stock.getProduct().getProducer().getCountry().getName())
                                    .build())
                            .trade(TradeDto.builder()
                                    .id(stock.getProduct().getProducer().getTrade().getId())
                                    .name(stock.getProduct().getProducer().getTrade().getName())
                                    .build())
                            .build())
                    .build())
            .shopDto(ShopDto.builder()
                    .id(stock.getShop().getId())
                    .name(stock.getShop().getName())
                    .countryDto(CountryDto.builder()
                            .id(stock.getShop().getCountry().getId())
                            .name(stock.getShop().getCountry().getName())
                            .build())
                    .build())
            .build();
  }

  public Stock mapStockDtoToStock(dto.StockDto stockDto) {

    return Stock.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .product(Product.builder()
                    .id(stockDto.getProductDto().getId())
                    .category(Category.builder()
                            .id(stockDto.getProductDto().getCategoryDto().getId())
                            .name(stockDto.getProductDto().getCategoryDto().getName())
                            .build())
                    .producer(Producer.builder()
                            .id(stockDto.getProductDto().getProducerDto().getId())
                            .name(stockDto.getProductDto().getProducerDto().getName())
                            .country(Country.builder()
                                    .id(stockDto.getProductDto().getProducerDto().getCountry().getId())
                                    .name(stockDto.getProductDto().getProducerDto().getCountry().getName())
                                    .build())
                            .trade(Trade.builder()
                                    .id(stockDto.getProductDto().getProducerDto().getTrade().getId())
                                    .name(stockDto.getProductDto().getProducerDto().getTrade().getName())
                                    .build())
                            .build())
                    .build())
            .shop(Shop.builder()
                    .id(stockDto.getShopDto().getId())
                    .name(stockDto.getShopDto().getName())
                    .country(Country.builder()
                            .id(stockDto.getShopDto().getCountryDto().getId())
                            .name(stockDto.getShopDto().getCountryDto().getName())
                            .build())
                    .build())
            .build();
  }
}
