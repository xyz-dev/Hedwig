/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

/**
 * @author Archer
 *
 */
public class AppProfile {

	private String domainName = InternalConstants.UNKONW_DOMAIN;
	private String serviceAppName = "defaultAppName";
	private String urlPattern = InternalConstants.HEDWIG_URL_PATTERN;
	private boolean assembleAppName = false;
	private int port = -1;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = HedwigUtil.filterString(domainName);
	}

	public String getServiceAppName() {
		return serviceAppName;
	}

	public void setServiceAppName(String serviceAppName) {
		this.serviceAppName = HedwigUtil.filterString(serviceAppName);
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = HedwigUtil.filterString(urlPattern);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAssembleAppName() {
		return assembleAppName;
	}

	public void setAssembleAppName(boolean assembleAppName) {
		this.assembleAppName = assembleAppName;
	}

}
