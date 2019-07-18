package repository.abstract_repository.entity;

import domain.Country;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface CountryRepository extends CrudRepository<Country, Long> {
  Optional<Country> findCountryByName(String name);

  void deleteCountryByName(String name);
}
