package cn.tamilin.api.client;

import static cn.tamilin.api.client.Constants.PRICE_CODE;
import static cn.tamilin.api.client.Constants.PRICE_CURRENCY;
import static cn.tamilin.api.client.Constants.PRICE_MULTIPLY;
import static cn.tamilin.api.client.Constants.UPLOAD_PAGE_SIZE;
import static cn.tamilin.api.client.Label.BRAND;
import static cn.tamilin.api.client.Label.CODE;
import static cn.tamilin.api.client.Label.ID;
import static cn.tamilin.api.client.Label.MODEL;
import static cn.tamilin.api.client.Label.NAME;
import static cn.tamilin.api.client.Label.OE;
import static cn.tamilin.api.client.Label.PRICE;
import static cn.tamilin.api.client.Label.QUANTITY;
import static cn.tamilin.api.client.Label.TYPE;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.json.Json.createReader;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.apache.commons.dbutils.BaseResultSetHandler;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;

public class ResultsHandler extends BaseResultSetHandler<List<String>> {

	private static final Logger logger = getLogger(ResultsHandler.class);

	private Configuration config = Configuration.getInstance();

	private List<String> successIds = new ArrayList<>();

	private Properties jdbc;

	private Connection h2conn;

	private JsonObject typeMapping;

	public ResultsHandler(Properties jdbc, Connection h2conn) {
		this.jdbc = jdbc;
		this.h2conn = h2conn;
		try {
			this.typeMapping = this.setTypejson();
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}
	}

	@Override
	protected List<String> handle() throws SQLException {
		JsonArrayBuilder products = createArrayBuilder();
		int c = 0, t = 0;
		while (next()) {
			JsonObjectBuilder parts = createObjectBuilder();
			String code = getStringValue(CODE);
			if (code == null || "".equals(code.trim()))
				code = getStringValue(OE);
			if (code == null || "".equals(code.trim()))
				continue;
			parts.add("code", code.trim());
			String name = getStringValue(NAME);
			parts.add("name", name == null ? code.trim() : name);
			parts.add("brand", getStringValue(BRAND));
			parts.add("type", this.getTypeValue());

			String oe = getStringValue(OE);
			if (oe != null && !"".equals(oe.trim())) {
				JsonArrayBuilder tags = createArrayBuilder();
				JsonObjectBuilder tag = createObjectBuilder();
				tag.add("code", "OE_CODE");
				tag.add("value", oe.trim());
				tags.add(tag);
				parts.add("tags", tags);
			}

			JsonObjectBuilder price = createObjectBuilder();
			price.add("code", PRICE_CODE);
			price.add("name", "");
			if (getMetaData().getColumnType(PRICE.value()) == Types.NULL)
				price.addNull("value");
			else {
				BigDecimal decimal = getBigDecimal(PRICE.value());
				if (decimal == null)
					price.addNull("value");
				else
					price.add("value", decimal.multiply(new BigDecimal(PRICE_MULTIPLY)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			}
			price.add("currency", PRICE_CURRENCY.getCurrencyCode());

			JsonArrayBuilder prices = createArrayBuilder();
			prices.add(price);

			JsonObjectBuilder product = createObjectBuilder();
			product.add("quantity", getInt(QUANTITY.value()));
			product.add("prices", prices);
			product.add("parts", parts);

			JsonArrayBuilder tags = createArrayBuilder();
			String model = getStringValue(MODEL);
			if (model != null && !"".equals(model.trim())) {
				JsonObjectBuilder tag = createObjectBuilder();
				tag.add("code", "CAR_MODEL");
				tag.add("value", model.trim());
				tags.add(tag);
			}
			String referenceId = getStringValue(ID);
			if (referenceId != null && !"".equals(referenceId)) {
				JsonObjectBuilder tag = createObjectBuilder();
				tag.add("code", "REFERENCEID");
				tag.add("value", referenceId.trim());
				tags.add(tag);
			}
			product.add("tags", tags);
			products.add(product);

			if (++c >= UPLOAD_PAGE_SIZE) {
				try {
					t += this.upload(products.build());
				} catch (IOException e) {
					logger.error(e.toString(), e);
				} finally {
					products = createArrayBuilder();
					c = 0;
				}
			}
		}

		if (c > 0)
			try {
				t += this.upload(products.build());
			} catch (IOException e) {
				logger.error(e.toString(), e);
			}
		logger.debug("upload product size:{} ,upload product success size:{}", t, this.successIds.size());
		return this.successIds;
	}

	private int upload(JsonArray products) throws IOException {
		JsonObjectBuilder json = createObjectBuilder();
		json.add("products", products);
		json.add("warehouse", config.getWarehouseId());

		HttpURLConnection conn = (HttpURLConnection) new URL(config.getUrlBase() + config.getUrlInventory()).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-API-Token", config.getApiToken());
		conn.setRequestProperty("X-Merchant-ID", valueOf(config.getMerchantId()));
		conn.setRequestProperty("X-API-Version", "0.2");
		conn.setRequestProperty("X-Transaction-ID", format("%d-%d", config.getMerchantId(), currentTimeMillis()));
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("User-Agent", format("%s/%s", getClass().getPackage().getImplementationTitle(), getClass().getPackage().getImplementationVersion()));
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(120000);
		conn.connect();

		String request = json.build().toString();
		logger.debug("Request {}", request);

		OutputStream out = conn.getOutputStream();
		out.write(request.getBytes());
		out.flush();

		int code = conn.getResponseCode();
		logger.debug("Response Code [{}]", code);
		try (InputStream in = conn.getInputStream()) {
			JsonReader reader = createReader(in);
			JsonObject response = reader.readObject();
			this.getSuccessReferenceId(response);
			logger.debug("Response {}", response.toString());
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		} finally {
			conn.disconnect();
		}
		return products.size();
	}

	private String getStringValue(Label label) {
		String value = null;
		try {
			value = getString(label.name());
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		}
		return (value == null || value.trim().equals("")) ? config.getDefaultValue(label) : value.trim();
	}

	private String getTypeValue() {
		try {
			String value = getString(TYPE.code());
			if (value != null && !"".equals(value.trim()))
				return value.trim();
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		}

		String type = this.getTypeByMapping();
		return type == null ? config.getDefaultValue(TYPE) : type;
	}

	private String getTypeByMapping() {
		String partsBrand = null;
		try {
			partsBrand = getString(BRAND.code());
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		}
		if (partsBrand == null || "".equals(partsBrand.trim()))
			return null;
		JsonObject typejson = this.typeMapping;
		if (typejson != null && typejson.containsKey(partsBrand))
			return typejson.getString(partsBrand);
		return null;
	}

	private void getSuccessReferenceId(JsonObject jsonObject) throws SQLException {
		if (jsonObject.containsKey("products")) {
			JsonArray products = jsonObject.getJsonArray("products");
			if (products != null && products.size() > 0) {
				QueryRunner runner = new QueryRunner();
				int successTime = Utils.unixTime();
				for (int i = 0; i < products.size(); ++i) {
					JsonObject product = products.getJsonObject(i);
					if (!product.containsKey("error")) {
						this.successIds.add(product.getString("REFERENCEID"));
						if (product.getInt("quantity") == 0)
							runner.update(h2conn, jdbc.getProperty("update.sql.success"), successTime, State.DELETED.value(), product.getString("REFERENCEID"));
						else
							runner.update(h2conn, jdbc.getProperty("update.sql.success"), successTime, State.ENABLED.value(), product.getString("REFERENCEID"));
					}
				}
			}
		}
	}

	private JsonObject setTypejson() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(config.getTypeMapping()).openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(120000);
		conn.connect();
		int code = conn.getResponseCode();
		logger.debug("Response Code [{}]", code);
		JsonObject response = null;
		try (InputStream in = conn.getInputStream()) {
			JsonReader reader = createReader(in);
			response = reader.readObject();
			logger.debug("Response {}", response.toString());
		} finally {
			conn.disconnect();
		}
		return response;
	}
}
