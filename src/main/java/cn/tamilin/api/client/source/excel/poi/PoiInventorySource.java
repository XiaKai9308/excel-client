package cn.tamilin.api.client.source.excel.poi;

import static java.util.stream.StreamSupport.stream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.InventorySource;
import cn.tamilin.api.client.source.excel.ExcelInventorySource;

public class PoiInventorySource extends ExcelInventorySource implements InventorySource {

	private Workbook workbook;

	@Override
	public Stream<Product> getStream() throws IOException {
		Path path = super.getExcelFilePath();
		logger.info("Parsing File {}", path);
		try {
			workbook = WorkbookFactory.create(path.toFile());
			Sheet sheet = workbook.getSheetAt(0);
			return stream(new PoiSheetSpliterator(sheet), false);
		} catch (EncryptedDocumentException | InvalidFormatException e) {
			throw new IOException("Failed to parse file " + path, e);
		}
	}

	@Override
	public void close() {
		if (workbook != null)
			try {
				workbook.close();
			} catch (IOException e) {
				logger.error(e.toString(), e);
			}
	}
}
