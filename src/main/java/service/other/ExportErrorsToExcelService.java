package service.other;

import domain.Error;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import service.entity.ErrorService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class ExportErrorsToExcelService {

  private final ErrorService errorService;

  public ExportErrorsToExcelService() {
    this.errorService = new ErrorService();
  }

  public void exportToExcel(final String filename) {

    if (filename == null || filename.equals("")) {
      throw new AppException("Filename is null or empty");

    }

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Exported errors. Date_" + LocalDateTime.now());

    Row row1 = sheet.createRow(0);
    row1.createCell(0).setCellValue("Time of occurence");
    row1.createCell(1).setCellValue("Error message");

    AtomicInteger rownNum = new AtomicInteger(1);
    errorService.getAllErrors().forEach(error -> {
      Row row = sheet.createRow((rownNum.getAndIncrement()));
      row.createCell(0).setCellValue(error.getDate().toString());
      row.createCell(1).setCellValue(error.getMessage());
    });


    try (FileOutputStream outputStream = new FileOutputStream(filename)) {
      workbook.write(outputStream);
    } catch (IOException e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException("Error during exporting errors to excel");
    }

    errorService.deleteAllErrors();
  }
}
