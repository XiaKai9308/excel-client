package cn.tamilin.api.client.source.excel;

import static java.nio.file.Files.find;
import static java.nio.file.Files.getLastModifiedTime;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import cn.tamilin.api.client.source.InventorySource;

public abstract class ExcelInventorySource implements InventorySource {

	protected final Logger logger = getLogger(getClass());

	protected Path getExcelFilePath() throws IOException {
		ExcelConfiguration conf = ExcelConfiguration.getInstance();
		Path dir = conf.getMonitorDir();
		String filename = conf.getFilenamePattern();
		Pattern pattern = "".equals(filename) ? null : compile(filename, CASE_INSENSITIVE);
		Optional<Path> opt = find(dir, 1, (p, a) -> {
			if (!a.isRegularFile())
				return false;
			if (pattern != null)
				return pattern.matcher(p.getFileName().toString()).find();
			return true;
		}).reduce((p1, p2) -> {
			try {
				return getLastModifiedTime(p1).compareTo(getLastModifiedTime(p2)) > 0 ? p1 : p2;
			} catch (IOException e) {
				logger.error(e.toString(), e);
				throw new RuntimeException(e);
			}
		});
		if (!opt.isPresent())
			throw new IOException("No File Found");
		return opt.get();
	}
}
