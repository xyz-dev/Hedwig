/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.config;

import java.util.Hashtable;
import java.util.concurrent.Executor;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer;
import com.yihaodian.configcentre.manager.ManagerListener;
import com.yihaodian.configcentre.manager.YccManager;
import com.yihaodian.configcentre.manager.impl.DefaultYccManager;

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
		YccGlobalPropertyConfigurer.init();
		YccManager ym = new DefaultYccManager(group, file, new ManagerListener() {

			@Override
			public void receiveConfigInfo(String configInfo) {

			}

			@Override
			public Executor getExecutor() {
				return null;
			}
		});
		String configInfo = ym.getAvailableConfigureInfomation(InternalConstants.DEFAULT_REQUEST_TIMEOUT);

		if (configInfo != null) {
			properites = YccGlobalPropertyConfigurer.loadProperties(configInfo);
		}

	}

	public String getProperty(String key, String defaultValue) {
		String value = properites.get(key);
		return value == null ? defaultValue : value;
	}


}
