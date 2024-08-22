package cn.tamilin.api.client;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;

import cn.tamilin.api.client.source.InventorySource;
import cn.tamilin.api.client.source.InventorySourceFactory;

public class Bootstrap {

	private static final Logger logger = getLogger(Bootstrap.class);

	private static final String ARG_MIGRATE = "migrate-from";

	public static void main(String[] args) throws IOException {
		DriverManager.setLogWriter(new PrintWriter(new SLF4jWriter(getLogger(DriverManager.class))));

		Map<String, String> arguments = parseArguments(args);

		String fromVersion = arguments.get(ARG_MIGRATE);
		if (fromVersion != null && !"".equals(fromVersion)) {
			logger.info("Migrating From Version [{}]", fromVersion);

			return;
		}

		int time = Utils.unixTime();
		try (InventorySource source = InventorySourceFactory.getInventorySource()) {
			InventoryConsumer consumer = new InventoryConsumer(time);
			try (Stream<Product> stream = source.getStream()) {
				stream.onClose(consumer).filter(p -> p != null).forEach(consumer);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		DataUploader dataupload = new DataUploader();
		dataupload.dataUploader();
	}

	public static Map<String, String> parseArguments(String[] args) {
		Pattern pattern = Pattern.compile("--([^\\s=]+)(=(\\S+)?)?");
		Map<String, String> map = new HashMap<String, String>();
		if (args == null || args.length == 0)
			return map;

		for (String arg : args) {
			Matcher matcher = pattern.matcher(arg);
			if (matcher.matches())
				map.put(matcher.group(1), matcher.group(3));
		}
		return map;
	}
}
