package dto;

import domain.Category;
import domain.Product;
import mapper.ModelMapper;
import repository.impl.ProductRepositoryImpl;
import service.entity.StockService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Test {

  public static void main(String[] args) {

    ProductRepositoryImpl productRepository = new ProductRepositoryImpl();

    List<Product> productsList = productRepository.findProductsByNameAndCategory("PC", Category.builder().name("COMPUTERS").build());

    StockService stockService = new StockService();
    Map<ProductDto, Set<ShopDto>> map2 = stockService.getProductDtoAndShopWithQuantityInStockMoreThan(productsList.stream().map(ModelMapper::mapProductToProductDto).collect(Collectors.toList()), 1);


    System.out.println(map2);
  }
}
