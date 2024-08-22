package cn.tamilin.api.client.source.excel;

import static cn.tamilin.api.client.Constants.EXCEL_DEFAULT_PROPERTIES;
import static cn.tamilin.api.client.Constants.EXCEL_PROPERTIES;
import static java.lang.Integer.parseInt;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;

import java.nio.file.Path;

import cn.tamilin.api.client.BaseConfiguration;

public class ExcelConfiguration extends BaseConfiguration {

	private static ExcelConfiguration INSTANCE = new ExcelConfiguration();

	public static ExcelConfiguration getInstance() {
		return INSTANCE;
	}

	public Path getMonitorDir() {
		return get(props.getProperty("directory").trim());
	}

	public String getFilenamePattern() {
		return props.getProperty("filename.pattern").trim();
	}

	public int getRowStart() {
		return parseInt(props.getProperty("row.start").trim());
	}

	public int getHeaderIndex() {
		return parseInt(props.getProperty("header.index").trim());
	}

	public String[] getColumnNames() {
		String names = props.getProperty("column.name").trim();
		return "".equals(names) ? null : names.split(",");
	}

	public int[] getColumnIndexes() {
		String indexes = props.getProperty("column.index").trim();
		return "".equals(indexes) ? null : stream(indexes.split(",")).mapToInt(Integer::parseInt).toArray();
	}

	private ExcelConfiguration() {
		super(EXCEL_PROPERTIES, EXCEL_DEFAULT_PROPERTIES);
	}
}
