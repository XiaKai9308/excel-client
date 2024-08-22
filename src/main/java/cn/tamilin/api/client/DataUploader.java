package cn.tamilin.api.client;

import static java.sql.DriverManager.getConnection;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;

public class DataUploader extends H2Properties{

	private static final Logger logger = getLogger(DataUploader.class);

	public void dataUploader() {
		Properties h2Props = getH2Properties();
		QueryRunner runner = new QueryRunner();
		try (Connection h2conn = getConnection(h2Props.getProperty("jdbc.h2"))){
			List<String> postIds = runner.query(h2conn, h2Props.getProperty("select.sql.post"), new ResultsHandler(h2Props ,h2conn), State.ENABLED.value());
			logger.debug("success products size is:{}, ids is :{}", postIds.size(), postIds.toString());
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}
}
