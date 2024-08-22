package cn.tamilin.api.client.source;

import static cn.tamilin.api.client.source.InventorySourceType.valueOf;

import java.io.IOException;
import java.sql.SQLException;

import cn.tamilin.api.client.Configuration;
import cn.tamilin.api.client.source.excel.jxl.JxlInventorySource;
import cn.tamilin.api.client.source.excel.poi.PoiInventorySource;
import cn.tamilin.api.client.source.jdbc.JdbcInventorySource;

public class InventorySourceFactory {

	private static Configuration config = Configuration.getInstance();

	public static InventorySource getInventorySource() throws SQLException, IOException {
		return getInventorySource(valueOf(config.getDataSource().toUpperCase()));
	}

	public static InventorySource getInventorySource(InventorySourceType type) throws SQLException, IOException {
		switch (type) {
		case DB:
			return new JdbcInventorySource();
		case POI:
			return new PoiInventorySource();
		case AGGR:
			return new AggregationInventorySource(getInventorySource(valueOf(config.getDataAggregationUpstream().toUpperCase())));
		case JXL:
			return new JxlInventorySource();
		default:
			throw new IllegalArgumentException("Unknown Inventory Source Type " + config.getDataSource());
		}
	}
}
