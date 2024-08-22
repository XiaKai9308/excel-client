package cn.tamilin.api.client;

import static cn.tamilin.api.client.Constants.H2_DEFAULT_PROPERTIES;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

public class H2Properties {

	private Properties h2Props = null;

	protected final Logger logger = getLogger(getClass());

	protected Properties getH2Properties(){
		if (h2Props == null) {
			h2Props = new Properties();
			try (InputStream in = Bootstrap.class.getClassLoader().getResourceAsStream(H2_DEFAULT_PROPERTIES)) {
				h2Props.load(in);
			}catch (IOException e) {
				logger.error(e.toString(), e);
			}
		}
		return h2Props;
	}
}
