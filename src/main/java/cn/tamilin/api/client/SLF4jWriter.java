package cn.tamilin.api.client;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;

public class SLF4jWriter extends Writer {

	private Logger logger;

	private StringBuffer buffer = new StringBuffer();

	public SLF4jWriter(Logger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		buffer.append(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		logger.info(buffer.toString().trim());
		buffer.setLength(0);
	}

	@Override
	public void close() throws IOException {
	}
}
