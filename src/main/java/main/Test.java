package main;

import service.other.DataInitializeService;
import service.other.ExportErrorsToExcelService;

public class Test {

  public static void main(String[] args) {
    new DataInitializeService().init();
    new ExportErrorsToExcelService().exportToExcel("errors5.xlsx");

  }
}
