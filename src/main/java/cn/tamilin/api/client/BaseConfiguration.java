package cn.tamilin.api.client;

import static cn.tamilin.api.client.Constants.DATA_DIR;
import static java.lang.System.exit;
import static java.nio.file.Paths.get;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;

public class BaseConfiguration {

	protected final Logger logger = getLogger(BaseConfiguration.class);

	protected Properties props = null;

	protected BaseConfiguration(String configFile) {
		this(configFile, null);
	}

	protected BaseConfiguration(String configFile, String defaultConfigFile) {
		props = this.loadConfigProperties(configFile, defaultConfigFile);
	}

	private Properties loadConfigProperties(String configFile, String defaultConfigFile) {
		Properties defaultProps = new Properties();

		if (defaultConfigFile != null)
			try (InputStream in = Bootstrap.class.getClassLoader().getResourceAsStream(defaultConfigFile)) {
				defaultProps.load(in);
			} catch (IOException | NullPointerException e) {
				logger.error("Config default properties NOT Found");
				exit(-1);
			}

		Properties config = new Properties(defaultProps);
		Path path = get(DATA_DIR, configFile);
		try (InputStream in = new FileInputStream(path.toFile())) {
			config.load(in);
		} catch (IOException | NullPointerException e) {
			logger.warn("Config properties [{}] NOT Found", path);
		}

		if (logger.isDebugEnabled())
			defaultProps.stringPropertyNames().forEach(key -> {
				logger.debug("Config Property [{}={}]", key, key.indexOf("token") == -1 ? config.getProperty(key) : "********");
			});

		return config;
	}
}
