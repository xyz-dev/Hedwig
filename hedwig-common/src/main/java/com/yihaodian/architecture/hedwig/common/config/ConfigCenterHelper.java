/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.config;

import java.util.Hashtable;

import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer;

/**
 * @author Archer
 *
 */
public class ConfigCenterHelper {


	private Hashtable<String, String> properites = new Hashtable<String, String>();

	public ConfigCenterHelper(String group, String file) {
		init(group, file);
	}

	public Hashtable<String, String> getProperites() {
		return properites;
	}

	private void init(String group, String file) {
		properites = YccGlobalPropertyConfigurer.getProperties(group, file, false);
	}

	public String getProperty(String key, String defaultValue) {
		String value = properites.get(key);
		return value == null ? defaultValue : value;
	}


}
