package cn.tamilin.api.client;

import static java.beans.Introspector.getBeanInfo;
import static org.slf4j.LoggerFactory.getLogger;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.slf4j.Logger;

import jxl.WorkbookSettings;

public class TestWorkbookSettings {

	private static final Logger logger = getLogger(TestWorkbookSettings.class);

	@Test
	public void test() throws IOException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		WorkbookSettings settings = new WorkbookSettings();
		for (PropertyDescriptor pd : getBeanInfo(WorkbookSettings.class).getPropertyDescriptors()) {
			logger.info("============================");
			logger.info("Name: " + pd.getName());
			Method method = pd.getWriteMethod();
			if (method == null)
				continue;
			logger.info("Method: " + method.getName());
			logger.info("Param: " + method.getParameterTypes()[0]);
			//logger.info(format("%s %s %s", pd.getName(), pd.getWriteMethod().getName(), pd.getWriteMethod().getParameterTypes()[0]));

			if ("windowProtected".equals(pd.getName())) {
				Object v = new Boolean("true");
				method.invoke(settings, v);
			}
		}
	}
}
