package cn.tamilin.api.client;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestXML {

	private static final Logger logger = getLogger(TestXML.class);

	private static final String BASE_URL = "http://maven.tamilin.cn";

	private static final String META_URL = BASE_URL + "/cn/tamilin/excel-client/maven-metadata.xml";

	private static final String ZIP_URL = BASE_URL + "/cn/tamilin/excel-client/%s/api-client-%s-bin.zip";

//	private static final String JAR_URL = BASE_URL + "/cn/tamilin/excel-client/%s/api-client-%s.jar";

	private static final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	private static final XPathFactory xpFactory = XPathFactory.newInstance();

	private static final Pattern LIB_PATTERN = Pattern.compile("/lib/.*\\.jar$");

	@Test
	public void test() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		String version = getVersion();
		logger.info("Version: {}", version);

//		Collection<String> libs = getLibs(version);
//		libs.forEach(lib -> logger.info(lib));

		URL url = new URL(String.format(ZIP_URL, version, version));
		URLConnection conn = url.openConnection();
		conn.setAllowUserInteraction(false);
		conn.setConnectTimeout(1000);
		conn.setReadTimeout(1000 * 60 * 5);
		conn.setUseCaches(false);
		conn.connect();
		try (ZipInputStream in = new ZipInputStream(conn.getInputStream())) {
			for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
				String name = entry.getName();
				if (LIB_PATTERN.matcher(name).find()) {
					logger.info("lib file: {} {}", name, entry.getCompressedSize());
					Path path = Paths.get("/Users/summer/Downloads", name);
					logger.info("Path: {}", path);
					createDirectories(path.getParent());
					copy(in, path, REPLACE_EXISTING);
				}
			}
		}
	}

//	public static Collection<String> getLibs(String version) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
//		URL url = new URL(String.format(JAR_URL, version, version));
//		URLConnection conn = url.openConnection();
//		conn.setAllowUserInteraction(false);
//		conn.setConnectTimeout(1000);
//		conn.setReadTimeout(1000 * 60 * 5);
//		conn.setUseCaches(false);
//		conn.connect();
//		try (JarInputStream in = new JarInputStream(conn.getInputStream())) {
//			Manifest manifest = in.getManifest();
//			String classPath = manifest.getMainAttributes().getValue("Class-Path");
//			return stream(classPath.split("\\s")).map(lib -> substringAfterLast(lib, "/")).collect(toSet());
//		}
//	}

	public static String getVersion() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		URL url = new URL(META_URL);
		URLConnection conn = url.openConnection();
		conn.setAllowUserInteraction(false);
		conn.setConnectTimeout(1000);
		conn.setReadTimeout(2000);
		conn.setUseCaches(false);
		conn.connect();
		try (InputStream in = conn.getInputStream()) {
			Document document = dbFactory.newDocumentBuilder().parse(in);
			String version = xpFactory.newXPath().evaluate("/metadata/versioning/release", document);
			return version;
		}
	}
}
