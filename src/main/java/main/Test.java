package main;

import configuration.DbConnection;
import service.other.DataInitializeService;

public class Test {

  public static void main(String[] args) {
    new DataInitializeService().init();

  }
}
