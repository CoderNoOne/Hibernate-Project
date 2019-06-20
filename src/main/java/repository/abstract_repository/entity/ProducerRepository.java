package repository.abstract_repository.entity;

import domain.Country;
import domain.Producer;
import domain.Product;
import domain.Trade;
import domain.enums.EGuarantee;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Long> {
  Optional<Producer> findByNameAndTradeAndCountry(String name, Trade trade, Country country);



}
