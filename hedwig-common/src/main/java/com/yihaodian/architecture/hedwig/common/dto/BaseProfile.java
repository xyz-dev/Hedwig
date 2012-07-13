/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.ProperitesContainer;
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
	private String rootPath = InternalConstants.BASE_ROOT;
	private String parentPath;
	private String serviceAppName = "defaultAppName";
	private String serviceName = "defaultServiceName";
	private String serviceVersion = "defaultVersion";

	public BaseProfile() {
		super();
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		String customerRoot = ProperitesContainer.getInstance().getProperty(PropKeyConstants.ZK_ROOT_PATH);
		if (!HedwigUtil.isBlankString(customerRoot)) {
			rootPath = customerRoot;
		}
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
		return "BaseProfile [rootPath=" + rootPath + ", parentPath=" + parentPath + ", serviceAppName=" + serviceAppName + ", serviceName="
				+ serviceName + ", serviceVersion=" + serviceVersion + "]";
	}

}
