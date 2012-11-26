/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;

/**
 * @author Archer Jiang
 * 
 */
public class BaseProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6856572567927129370L;
	protected String rootPath = InternalConstants.BASE_ROOT;
	protected String domainName = InternalConstants.UNKONW_DOMAIN;
	protected String parentPath;
	protected String serviceAppName = "defaultAppName";
	protected String serviceName = "defaultServiceName";
	protected String serviceVersion = "defaultVersion";

	public BaseProfile() {
		super();
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getServiceAppName() {
		return serviceAppName;
	}

	public void setServiceAppName(String serviceAppName) {
		this.serviceAppName = serviceAppName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public String getParentPath() {
		if (HedwigUtil.isBlankString(parentPath)) {
			try {
				parentPath = ZkUtil.createParentPath(this);
			} catch (InvalidParamException e) {

			}
		}
		return parentPath;
	}

	@Override
	public String toString() {
		return "\nBaseProfile [rootPath=" + rootPath + ", parentPath=" + parentPath + ", serviceAppName="
				+ serviceAppName + ", serviceName="
				+ serviceName + ", serviceVersion=" + serviceVersion + "]";
	}

}
