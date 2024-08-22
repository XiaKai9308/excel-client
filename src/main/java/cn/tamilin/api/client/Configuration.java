package cn.tamilin.api.client;

import static cn.tamilin.api.client.Constants.CONFIG_DEFAULT_PROPERTIES;
import static cn.tamilin.api.client.Constants.CONFIG_PROPERTIES;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public class Configuration extends BaseConfiguration {

	private static Configuration INSTANCE = new Configuration();

	public static Configuration getInstance() {
		return INSTANCE;
	}

	public int getMerchantId() {
		return parseInt(props.getProperty("merchant.id"));
	}

	public int getWarehouseId() {
		return parseInt(props.getProperty("warehouse.id"));
	}

	public String getApiToken() {
		return props.getProperty("api.token").trim();
	}

	public String getUrlBase() {
		return props.getProperty("api.url.base").trim();
	}

	public String getTypeMapping() {
		return props.getProperty("api.type.mapping").trim();
	}

	public String getUrlInventory() {
		return props.getProperty("api.url.inventory").trim();
	}

	public String getDataSource() {
		return props.getProperty("data.source").trim();
	}

	public String getDataAggregationUpstream() {
		return props.getProperty("data.aggregation.upstream").trim();
	}

	public String getDefaultValue(Label label) {
		String value = props.getProperty(format("default.%s", label.code()));
		return (value == null) ? null : value.trim();
	}

	private Configuration() {
		super(CONFIG_PROPERTIES, CONFIG_DEFAULT_PROPERTIES);
	}
}
