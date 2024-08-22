package cn.tamilin.api.client.source.jdbc;

import static cn.tamilin.api.client.Label.BRAND;
import static cn.tamilin.api.client.Label.CODE;
import static cn.tamilin.api.client.Label.ID;
import static cn.tamilin.api.client.Label.MODEL;
import static cn.tamilin.api.client.Label.NAME;
import static cn.tamilin.api.client.Label.OE;
import static cn.tamilin.api.client.Label.PRICE;
import static cn.tamilin.api.client.Label.QUANTITY;
import static cn.tamilin.api.client.Label.TYPE;
import static java.lang.Long.MAX_VALUE;
import static java.sql.Types.NULL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import cn.tamilin.api.client.Label;
import cn.tamilin.api.client.Product;

public class ResultSetSpliterator extends AbstractSpliterator<Product> {

	private ResultSet rs;

	protected ResultSetSpliterator(ResultSet rs) {
		super(MAX_VALUE, ORDERED);
		this.rs = rs;
	}

	@Override
	public boolean tryAdvance(Consumer<? super Product> action) {
		try {
			if (rs.next()) {
				//GenerousBeanProcessor processor = new GenerousBeanProcessor();
				//Product product = processor.toBean(rs, Product.class);
				Product product = new Product();
				product.setId(getString(ID));
				product.setCode(getString(CODE));
				product.setOe(getString(OE));
				product.setName(getString(NAME));
				product.setBrand(getString(BRAND));
				product.setType(getString(TYPE));
				product.setModel(getString(MODEL));
				product.setQuantity(rs.getInt(QUANTITY.code()));
				if (rs.getMetaData().getColumnType(PRICE.value()) == NULL)
					product.setPrice(null);
				else
					product.setPrice(rs.getBigDecimal(PRICE.value()));
				action.accept(product);
				return true;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	private String getString(Label label) throws SQLException {
		return rs.getString(label.code()) == null ? null : rs.getString(label.code()).trim();
	}
}
