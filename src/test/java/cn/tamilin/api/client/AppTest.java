package cn.tamilin.api.client;

import static cn.tamilin.api.client.Constants.DATA_DIR;
import static cn.tamilin.api.client.Constants.JDBC_DEFAULT_PROPERTIES;
import static cn.tamilin.api.client.Constants.JDBC_PROPERTIES;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;

import cn.tamilin.api.client.Bootstrap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	private static final Logger logger = getLogger(AppTest.class);

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public void testApp() throws SQLException, IOException {
		Properties props = loadJdbcProperties();
		try (Connection conn = DriverManager.getConnection(props.getProperty("jdbc.h2"))) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM product");
			ResultSetMetaData meta = rs.getMetaData();
			String[] columns = new String[meta.getColumnCount()];
			for (int i = meta.getColumnCount(); i > 0; --i)
				columns[i - 1] = meta.getColumnLabel(i);
			while (rs.next()) {
				Stream.of(columns).forEach(label -> {
					try {
						logger.debug("{} : {}", label, rs.getObject(label));
					} catch (SQLException e) {
						logger.error(e.toString(), e);
					}
				});
				logger.debug("========================================");
			}
		}
	}

	private Properties loadJdbcProperties() throws IOException {
		Properties defaultProps = new Properties();
		try (InputStream in = Bootstrap.class.getClassLoader().getResourceAsStream(JDBC_DEFAULT_PROPERTIES)) {
			defaultProps.load(in);
		}

		Properties jdbc = new Properties(defaultProps);
		Path path = Paths.get(DATA_DIR, JDBC_PROPERTIES);
		try (InputStream in = new FileInputStream(path.toFile())) {
			jdbc.load(in);
		} catch (IOException | NullPointerException e) {
			logger.warn("JDBC properties [{}] NOT Found", path);
		}

		if (logger.isDebugEnabled())
			defaultProps.stringPropertyNames().forEach(key -> {
				logger.debug("JDBC Property [{}={}]", key, key.indexOf("password") == -1 ? jdbc.getProperty(key) : "********");
			});

		return jdbc;
	}
}
