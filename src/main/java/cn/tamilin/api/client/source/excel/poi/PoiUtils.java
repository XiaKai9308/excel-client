package cn.tamilin.api.client.source.excel.poi;

import static cn.tamilin.api.client.Utils.NUMBER_FORMAT;

import org.apache.poi.ss.usermodel.Cell;

public class PoiUtils {

	public static String getString(Cell cell, String defaultValue) {
		String value = getString(cell);
		return value == null ? defaultValue : value;
	}

	public static String getString(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case NUMERIC:
			return NUMBER_FORMAT.format(cell.getNumericCellValue());
		case FORMULA:
			switch (cell.getCachedFormulaResultTypeEnum()) {
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case NUMERIC:
				return NUMBER_FORMAT.format(cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue().trim();
			case ERROR:
			default:
				return null;
			}
		case STRING:
			return cell.getStringCellValue().trim();
		case _NONE:
		case BLANK:
		case ERROR:
		default:
			return null;
		}
	}

	public static Number getNumber(Cell cell, Number defaultValue) {
		Number value = getNumber(cell);
		return value == null ? defaultValue : value;
	}

	public static Number getNumber(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue() ? 1 : 0;
		case NUMERIC:
			return cell.getNumericCellValue();
		case FORMULA:
			switch (cell.getCachedFormulaResultTypeEnum()) {
			case BOOLEAN:
				return cell.getBooleanCellValue() ? 1 : 0;
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				try {
					return Double.parseDouble(cell.getStringCellValue());
				} catch (NumberFormatException e) {
					return null;
				}
			case ERROR:
			default:
				return null;
			}
		case STRING:
			try {
				return Double.parseDouble(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				return null;
			}
		case _NONE:
		case BLANK:
		case ERROR:
		default:
			return null;
		}
	}
}
