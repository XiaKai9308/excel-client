package cn.tamilin.api.client;

import static java.sql.DriverManager.getConnection;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.Logger;

public class InventoryConsumer extends H2Properties implements Consumer<Product>, Runnable {

	private static final Logger logger = getLogger(InventoryConsumer.class);

	private int time;

	private QueryRunner runner = new QueryRunner();

	private Connection h2conn;

	private Properties h2Props = getH2Properties();

	public InventoryConsumer(int time) {
		this.time = time;

		try {
			this.h2conn = getConnection(h2Props.getProperty("jdbc.h2"));
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		}
	}

	@Override
	public void accept(Product t) {
		try {
			Product product = runner.query(h2conn, h2Props.getProperty("select.sql.id"), new BeanHandler<Product>(Product.class), t.getId());
			if (product == null) {
				Object addParams[] = { t.getId(), t.getCode(), t.getOe(), t.getName(), t.getBrand(), t.getType(), t.getModel(), t.getQuantity(), t.getPrice(), this.time, 0, State.ENABLED.value() };
				runner.insert(h2conn, h2Props.getProperty("insert.sql"), new BeanHandler<Product>(Product.class), addParams);
			} else {
				if (product.getSuccess_time() >= product.getLast_update()) {
					if (isSame(product, t)) {
						Object updateParams[] = { this.time, this.time, t.getId() };
						runner.update(h2conn, h2Props.getProperty("update.sql.nochange"), updateParams);
						return;
					}
				}
				Object updateParams[] = { t.getOe(), t.getName(), t.getModel(), t.getQuantity(), t.getPrice(), this.time, State.ENABLED.value(), t.getId() };
				runner.update(h2conn, h2Props.getProperty("update.sql.change"), updateParams);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	private boolean isSame(Product p1, Product p2) {
		if (p1.getQuantity() != p2.getQuantity())
			return false;
		if (!priceEquals(p1.getPrice(),p2.getPrice()))
			return false;
		if (!valueEquals(p1.getName(), p2.getName()))
			return false;
		if (!valueEquals(p1.getModel(), p2.getModel()))
			return false;
		if (!valueEquals(p1.getOe(), p2.getOe()))
			return false;
		return true;
	}

	private boolean valueEquals(Object s1, Object s2) {
		return (s1 == null && s2 == null) ? true : (s1 != null && s1.equals(s2));
	}

	private boolean priceEquals(BigDecimal s1, BigDecimal s2) {
		return (s1 == null && s2 == null) ? true : (s1 != null && ((s1.compareTo(s2) == 0) ? true : false));
	}

	@Override
	public void run() {
		try {
			Object updateParams[] = { 0, this.time, this.time, State.ENABLED.value() };
			int update = runner.update(h2conn, h2Props.getProperty("update.sql.nosearch"), updateParams);
			logger.debug("no search ids size in h2 is : {}", update);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			closeQuietly(h2conn);
		}
	}
}
