package cn.tamilin.api.client;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.Test;
import org.slf4j.Logger;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class TestJXL {

	private static final Logger logger = getLogger(TestJXL.class);

	@Test
	public void test() throws BiffException, IOException {
		try (InputStream fin = new FileInputStream("/Users/pennix/Downloads/excel/inventory.xls")) {
			Workbook wb = Workbook.getWorkbook(fin);
			Sheet[] sheets = wb.getSheets();
			for (Sheet sheet : sheets) {
				int rows = sheet.getRows();
				logger.info("Sheet: {}", sheet.getName());
				for (int i = 0; i < rows; ++i) {
					Cell[] row = sheet.getRow(i);
					Cell cell = row[1];
					//for (Cell cell : row) {
						logger.info("({},{}) {} {} {}", cell.getRow(), cell.getColumn(), cell.getType(), cell.getClass(), cell.getContents());
						if (cell instanceof NumberCell) {
							NumberFormat format = ((NumberCell) cell).getNumberFormat();
							logger.info("Format {}", format);
						}
					//}
				}
			}
			wb.close();
		}
	}

	@Test
	public void testFormat() {
		double d = 1234567890.1234567890;
		NumberFormat format = new DecimalFormat("#.######");
		System.out.println(String.valueOf(d));
		System.out.println(format.format(d));
	}
}
