package cn.tamilin.api.client.source.jdbc;

import static cn.tamilin.api.client.Constants.DATA_DIR;
import static cn.tamilin.api.client.Constants.JDBC_DEFAULT_PROPERTIES;
import static cn.tamilin.api.client.Constants.JDBC_PROPERTIES;
import static java.nio.file.Paths.get;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.InventorySource;

public class JdbcInventorySource implements InventorySource {

	private static final Logger logger = getLogger(JdbcInventorySource.class);

	private Connection conn;

	private String sql;

	private Statement stmt;

	private ResultSet rs;

	public JdbcInventorySource() throws SQLException, IOException {
		super();
		Properties jdbc = loadJdbcProperties();
		conn = DriverManager.getConnection(jdbc.getProperty("jdbc.url"), jdbc.getProperty("jdbc.username"), jdbc.getProperty("jdbc.password"));
		sql = jdbc.getProperty("sql");
	}

	@Override
	public Stream<Product> getStream() throws IOException {
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return stream(new ResultSetSpliterator(rs), false);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	private Properties loadJdbcProperties() throws IOException {
		Properties defaultProps = new Properties();
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(JDBC_DEFAULT_PROPERTIES)) {
			defaultProps.load(in);
		}

		Properties jdbc = new Properties(defaultProps);
		Path path = get(DATA_DIR, JDBC_PROPERTIES);
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

	protected Connection getConnection() {
		return conn;
	}

	@Override
	public void close() {
		closeQuietly(conn, stmt, rs);
	}
}
