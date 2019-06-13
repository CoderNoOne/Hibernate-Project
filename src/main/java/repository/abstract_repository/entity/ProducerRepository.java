package repository.abstract_repository.entity;

import domain.Country;
import domain.Producer;
import domain.Trade;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Long> {
  Optional<Producer> findByNameAndTradeAndCountry(String name, Trade trade, Country country);
}
