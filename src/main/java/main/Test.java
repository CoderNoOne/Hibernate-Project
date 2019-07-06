package main;

import domain.Country;
import domain.Customer;
import service.entity.CustomerService;

public class Test {

  public static void main(String[] args) {

    new CustomerService().deleteCustomer(Customer.builder().country(Country.builder().name("POLAND").build())
            .name("KASIA")
            .surname("KRUK")
            .build());
  }
}
