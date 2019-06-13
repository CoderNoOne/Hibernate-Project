package repository.abstract_repository.entity;

import domain.Trade;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface TradeRepository extends CrudRepository<Trade, Long> {

  Optional<Trade> findByName(String name);
}
