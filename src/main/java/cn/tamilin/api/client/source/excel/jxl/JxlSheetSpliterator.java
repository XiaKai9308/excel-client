package cn.tamilin.api.client.source.excel.jxl;

import static cn.tamilin.api.client.source.excel.jxl.JxlUtils.getNumber;
import static cn.tamilin.api.client.source.excel.jxl.JxlUtils.getString;
import static java.util.Arrays.fill;
import static java.util.Arrays.stream;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.excel.ExcelSheetSpliterator;
import jxl.Cell;
import jxl.Sheet;

public class JxlSheetSpliterator extends ExcelSheetSpliterator<Cell[]> {

	private Sheet sheet;

	private int current = config.getRowStart();

	private int total;

	private int[] indexes;

	protected JxlSheetSpliterator(Sheet sheet) {
		this.sheet = sheet;
		this.total = sheet.getRows();
		this.indexes = this.getIndexes();
	}

	@Override
	protected Cell[] getNextRowFromExcel() {
		if (current + 1 >= total)
			return null;
		return sheet.getRow(current++);
	}

	@Override
	protected Product rowToProduct(Cell[] row) {
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

//		Number price = this.getNumberValue(row, 8);
		String price = this.getStringValue(row, 8);
		if (price == null || "".equals(price))
			product.setPrice(null);
		else {
			product.setPrice(new BigDecimal(price));
		}
		return product;
	}

	private String getStringValue(Cell[] row, int index) {
		return indexes[index] > -1 ? getString(row[indexes[index]]) : null;
	}

	private Number getNumberValue(Cell[] row, int index) {
		if (indexes[index] < 0)
			return null;
		return getNumber(row[indexes[index]]);
	}

	private int[] getIndexes() {
		int[] indexes = config.getColumnIndexes();
		if (indexes != null)
			return indexes;

		int headerIndex = config.getHeaderIndex();
		String[] names = config.getColumnNames();
		indexes = new int[names.length];
		fill(indexes, -1);

		Cell[] header = sheet.getRow(headerIndex);
		if (header != null && logger.isInfoEnabled())
			logger.info("Sheet Header [{}]", stream(header).map(cell -> cell.getContents()).collect(Collectors.joining(", ")));
		for (int index = header.length - 1; index >= 0; --index) {
			String text = header[index].getContents().trim();
			for (int i = 0; i < names.length; ++i) {
				if (names[i].equals(text))
					indexes[i] = index;
			}
		}
		return indexes;
	}
}
