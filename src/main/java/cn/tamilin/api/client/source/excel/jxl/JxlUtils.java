package cn.tamilin.api.client.source.excel.jxl;

import static cn.tamilin.api.client.Utils.NUMBER_FORMAT;
import static java.lang.Double.parseDouble;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.LabelCell;
import jxl.NumberCell;

public class JxlUtils {

	public static String getString(Cell cell, String defaultValue) {
		String value = getString(cell);
		return value == null ? defaultValue : value;
	}

	public static String getString(Cell cell) {
		if (cell == null)
			return null;
		if (cell instanceof NumberCell)
			return NUMBER_FORMAT.format(((NumberCell) cell).getValue());
		else if (cell instanceof LabelCell)
			return ((LabelCell) cell).getString().trim();
		else
			return cell.getContents();
	}

	public static Number getNumber(Cell cell, Number defaultValue) {
		Number value = getNumber(cell);
		return value == null ? defaultValue : value;
	}

	public static Number getNumber(Cell cell) {
		if (cell == null)
			return null;
		if (cell instanceof NumberCell)
			return ((NumberCell) cell).getValue();
		else if (cell instanceof BooleanCell)
			return ((BooleanCell) cell).getValue() ? 1 : 0;
		else if (cell instanceof LabelCell)
			try {
				return parseDouble(((LabelCell) cell).getString());
			} catch (NumberFormatException e) {
				return null;
			}
		else
			try {
				return parseDouble(cell.getContents());
			} catch (NumberFormatException e) {
				return null;
			}
	}
}
