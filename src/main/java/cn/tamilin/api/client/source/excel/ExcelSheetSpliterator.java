package cn.tamilin.api.client.source.excel;

import static java.lang.Long.MAX_VALUE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import org.slf4j.Logger;

import cn.tamilin.api.client.Product;

public abstract class ExcelSheetSpliterator<E> extends AbstractSpliterator<Product> {

	protected final Logger logger = getLogger(getClass());

	protected ExcelConfiguration config = ExcelConfiguration.getInstance();

	protected abstract E getNextRowFromExcel();

	protected abstract Product rowToProduct(E row);

	protected ExcelSheetSpliterator() {
		super(MAX_VALUE, ORDERED);
	}

	@Override
	public boolean tryAdvance(Consumer<? super Product> action) {
		E row = this.getNextRowFromExcel();
		if (row == null)
			return false;
		Product product = this.rowToProduct(row);
		action.accept(product);
		return true;
	}
}
