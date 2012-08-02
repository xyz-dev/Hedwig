/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

/**
 * @author Archer
 * 
 */
public class HedwigClientFactoryBean extends HedwigEventInterceptor implements FactoryBean {

	private Object serviceProxy;
	private String appName;
	private String serviceName;
	private String serviceVersion;
	private String target;

	@Override
	public Object getObject() throws Exception {
		return serviceProxy;
	}

	@Override
	public Class getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		if (clientProfile == null) {
			clientProfile = createClientProfile();
		}
		super.afterPropertiesSet();
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
	}

	private ClientProfile createClientProfile() throws InvalidParamException {
		ClientProfile p = new ClientProfile();
		if (HedwigUtil.isBlankString(target)) {
			if (HedwigUtil.isBlankString(appName)) {
				throw new InvalidParamException("appName must not blank!!!");
			}
			p.setServiceAppName(appName);
			if (HedwigUtil.isBlankString(serviceName)) {
				throw new InvalidParamException("serviceName must not blank!!!");
			}
			p.setServiceName(serviceName);
			if (HedwigUtil.isBlankString(serviceVersion)) {
				throw new InvalidParamException("serviceVersion must not blank!!!");
			}
			p.setServiceVersion(serviceVersion);
		} else {
			p.setTarget(target);
		}

		return p;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
