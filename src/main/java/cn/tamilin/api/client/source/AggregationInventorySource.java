package cn.tamilin.api.client.source;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.slf4j.Logger;

import cn.tamilin.api.client.Product;
import cn.tamilin.api.client.source.jdbc.JdbcInventorySource;

public class AggregationInventorySource extends JdbcInventorySource {

	private static final Logger logger = getLogger(AggregationInventorySource.class);

	private static final String SQL_INSERT = "insert into product(id,code,oe,name,brand,type,model,quantity,price) values (?,?,?,?,?,?,?,?,?)";

	private InventorySource upstream;

	public AggregationInventorySource(InventorySource upstream) throws SQLException, IOException {
		super();
		this.upstream = upstream;
	}

	@Override
	public Stream<Product> getStream() throws IOException {
		Connection conn = super.getConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
			upstream.getStream().filter(p -> p != null).forEach(p -> {
				try {
					int i = 0;
					pstmt.setString(++i, p.getId());
					pstmt.setString(++i, p.getCode());
					pstmt.setString(++i, p.getOe());
					pstmt.setString(++i, p.getName());
					pstmt.setString(++i, p.getBrand());
					pstmt.setString(++i, p.getType());
					pstmt.setString(++i, p.getModel());
					pstmt.setInt(++i, p.getQuantity());
					pstmt.setBigDecimal(++i, p.getPrice());
					pstmt.addBatch();
				} catch (SQLException e) {
					logger.error(e.toString(), e);
				}
			});
			pstmt.executeBatch();
		} catch (SQLException e) {
			logger.error(e.toString(), e);
		}
		return super.getStream();
	}
}
