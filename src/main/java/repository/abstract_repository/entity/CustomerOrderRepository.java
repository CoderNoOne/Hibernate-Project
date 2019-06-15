package repository.abstract_repository.entity;

import domain.CustomerOrder;
import repository.abstract_repository.base.CrudRepository;

public interface CustomerOrderRepository extends CrudRepository <CustomerOrder, Long> {
}
