package cn.tamilin.api.client.source.excel.poi;

import static cn.tamilin.api.client.source.excel.poi.PoiUtils.getNumber;
import static cn.tamilin.api.client.source.excel.poi.PoiUtils.getString;
import static java.util.Arrays.fill;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.excel.ExcelSheetSpliterator;

public class PoiSheetSpliterator extends ExcelSheetSpliterator<Row> {

	private Sheet sheet;

	private int current = config.getRowStart();

	private int lastRowNum;

	private int[] indexes;

	public PoiSheetSpliterator(Sheet sheet) {
		super();
		this.sheet = sheet;
		this.lastRowNum = sheet.getLastRowNum();
		this.indexes = this.getIndexes();
	}

	@Override
	protected Row getNextRowFromExcel() {
		if (current > lastRowNum)
			return null;
		return sheet.getRow(current++);
	}

	@Override
	protected Product rowToProduct(Row row) {
		Product product = new Product();
		product.setId(getStringValue(row, 0));
		product.setCode(getStringValue(row, 1));
		product.setOe(getStringValue(row, 2));
		product.setName(getStringValue(row, 3));
		product.setBrand(getStringValue(row, 4));
		product.setType(getStringValue(row, 5));
		product.setModel(getStringValue(row, 6));

		Number quantity = this.getNumberValue(row, 7);
		if (quantity == null)
			return null;
		product.setQuantity(quantity.intValue());

		String price = this.getStringValue(row, 8);
		if (price == null || "".equals(price))
			product.setPrice(null);
		else {
			product.setPrice(new BigDecimal(price));
		}
		return product;
	}

	private String getStringValue(Row row, int index) {
		return indexes[index] > -1 ? getString(row.getCell(indexes[index])) : null;
	}

	private Number getNumberValue(Row row, int index) {
		if (indexes[index] < 0)
			return null;
		return getNumber(row.getCell(indexes[index]));
	}

	private int[] getIndexes() {
		int[] indexes = config.getColumnIndexes();
		if (indexes != null)
			return indexes;

		int headerIndex = config.getHeaderIndex();
		String[] names = config.getColumnNames();
		indexes = new int[names.length];
		fill(indexes, -1);

		Row header = sheet.getRow(headerIndex);
		for (int index = header.getLastCellNum(); index >= 0; --index) {
			String text = PoiUtils.getString(header.getCell(index));
			for (int i = 0; i < names.length; ++i)
				if (names[i].equals(text))
					indexes[i] = index;
		}
		return indexes;
	}
}
