package cn.tamilin.api.client.source;

import java.io.IOException;
import java.util.stream.Stream;

import cn.tamilin.api.client.Product;

public interface InventorySource extends AutoCloseable {

	public Stream<Product> getStream() throws IOException;
}
