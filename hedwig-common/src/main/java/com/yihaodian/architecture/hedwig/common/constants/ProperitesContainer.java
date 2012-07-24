/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.util.SystemUtil;

/**
 * @author Archer Jiang
 * 
 */
public class ProperitesContainer {
	private static Logger logger = LoggerFactory
			.getLogger(ProperitesContainer.class);
	public static Properties HATHAWAY_ENV = new Properties();
	private static ProperitesContainer container = new ProperitesContainer();

	public static ProperitesContainer getInstance() {
		return container;
	}

	private static final int SOURCE_CLASSPATH = 0;
	private static final int SOURCE_ZK = 1;
	private static int loadSource = SOURCE_CLASSPATH;

	private static String fileName;

	private ProperitesContainer() {
		fileName = InternalConstants.PROPERITIES_FILE_NAME;
		load();
	}

	public void loadFromClassPath() {
		loadSource = SOURCE_CLASSPATH;
		InputStream input = null;
		Properties p = new Properties();
		String path = System
				.getProperty(InternalConstants.PROPERITIES_PATH_KEY);
		try {
			File file = new File(path);
			if (file.exists()) {
				input = new FileInputStream(file);
			} else {
				ClassLoader clzLoader = this.getClass().getClassLoader();
				URL url = clzLoader.getSystemResource(fileName);
				if (url != null) {
					input = url.openStream();
				} else {
					input = clzLoader.getSystemResourceAsStream(fileName);
				}
			}
			if (input != null) {
				p.load(input);
				if (!p.isEmpty()) {
					HATHAWAY_ENV = p;
				} else {
					logger.debug("Load properties file:" + fileName
							+ " failed!!!");
				}
			}
		} catch (IOException e) {
			logger.error("Load properties file:" + fileName + " failed!!!", e);
			System.exit(1);
		}
	}

	public void loadFromZK() {
		loadSource = SOURCE_ZK;
		// TODO load properties from remote server.
	}

	public void load() {
		switch (loadSource) {
		case SOURCE_ZK:
			loadFromZK();
			break;

		default:
			loadFromClassPath();
			break;
		}
		HATHAWAY_ENV.put(PropKeyConstants.JVM_PID, SystemUtil.getJvmPid());
		HATHAWAY_ENV.put(PropKeyConstants.HOST_IP, SystemUtil.getLocalhostIp());

	}


	public String getProperty(String key) {
		if (HATHAWAY_ENV.isEmpty()) {
			load();
		}
		return HATHAWAY_ENV.getProperty(key);
	}

	public String getProperty(String key, String defValue) {
		String value = getProperty(key);
		return value == null ? defValue : value.trim();
	}

	public int getIntProperty(String key, int defValue) {
		int v = defValue;
		String value = getProperty(key);
		if (value != null) {
			try {
				v = Integer.valueOf(value.trim());
			} catch (Exception e) {
			}
		}
		return v;
	}

	public long getLongProperty(String key, long defValue) {
		long v = defValue;
		String value = getProperty(key);
		if (value != null) {
			try {
				v = Long.valueOf(value.trim());
			} catch (Exception e) {
			}
		}
		return v;
	}

}
