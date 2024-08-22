package cn.tamilin.api.client.source.excel.jxl;

import static cn.tamilin.api.client.Constants.DATA_DIR;
import static cn.tamilin.api.client.Constants.JXL_PROPERTIES;
import static java.beans.Introspector.getBeanInfo;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.stream.StreamSupport.stream;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.InventorySource;
import cn.tamilin.api.client.source.excel.ExcelInventorySource;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class JxlInventorySource extends ExcelInventorySource implements InventorySource {

	private Workbook workbook;

	@Override
	public Stream<Product> getStream() throws IOException {
		Path path = super.getExcelFilePath();
		logger.info("Parsing File {}", path);
		try {
			workbook = Workbook.getWorkbook(path.toFile(), getWorkbookSettings());
			Sheet sheet = workbook.getSheet(0);
			return stream(new JxlSheetSpliterator(sheet), false);
		} catch (BiffException e) {
			throw new IOException("Failed to parse file " + path, e);
		}
	}

	private WorkbookSettings getWorkbookSettings() {
		WorkbookSettings settings = new WorkbookSettings();

		Path path = get(DATA_DIR, JXL_PROPERTIES);
		if (!exists(path))
			return settings;

		Properties config = new Properties();
		try (InputStream in = newInputStream(path)) {
			config.load(in);
			for (PropertyDescriptor propertyDescriptor : getBeanInfo(WorkbookSettings.class).getPropertyDescriptors()) {
				String name = propertyDescriptor.getName();
				if (!config.containsKey(name))
					continue;
				String value = config.getProperty(name).trim();
				if ("".equals(value))
					continue;
				Method method = propertyDescriptor.getWriteMethod();
				if (method == null)
					continue;
				if (method.getParameterCount() != 1)
					continue;

				Class<?> type = method.getParameterTypes()[0];
				Object param = value;
				if (type == Boolean.TYPE)
					param = Boolean.parseBoolean(value);
				else if (type == Integer.TYPE)
					param = Integer.parseInt(value);

				if (param != null)
					try {
						logger.debug("Jxl Setting [{}]: {}", name, param);
						method.invoke(settings, param);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						logger.error(e.toString(), e);
					}
			}
		} catch (IOException | NullPointerException | IntrospectionException e) {
			logger.warn("Failed jxl settings from {}", path, e);
		}

		return settings;
	}

	@Override
	public void close() {
		if (workbook != null)
			workbook.close();
	}
}
