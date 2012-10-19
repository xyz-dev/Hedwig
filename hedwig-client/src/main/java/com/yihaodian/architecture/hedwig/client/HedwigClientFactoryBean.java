/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import java.util.Set;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

/**
 * @author Archer
 * 
 */
public class HedwigClientFactoryBean extends HedwigEventInterceptor implements FactoryBean {

	private Object serviceProxy;
	private String serviceAppName;
	private String domainName;
	private String serviceName;
	private String serviceVersion;
	private String target;
	private String clientAppName;
	private Long timeout;
	private Set<String> noRetryMethods;

	public HedwigClientFactoryBean() {
		super();
	}

	public HedwigClientFactoryBean(Class<?> clazz, String domainName, String serviceAppName, String serviceName,
			String serviceVersion,
			String clientAppName, Long timeout) throws Exception {
		super();
		this.serviceInterface = clazz;
		this.serviceAppName = serviceAppName;
		this.domainName = domainName;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.clientAppName = clientAppName;
		this.timeout = timeout;
		afterPropertiesSet();
	}

	public HedwigClientFactoryBean(Class<?> clazz, String domainName, String serviceAppName, String serviceName,
			String serviceVersion, String clientAppName, String user, String password, Long timeout) throws Exception {
		super();
		this.serviceInterface = clazz;
		this.serviceAppName = serviceAppName;
		this.domainName = domainName;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.clientAppName = clientAppName;
		this.timeout = timeout;
		afterPropertiesSet();
	}

	public HedwigClientFactoryBean(Class<?> clazz, String target, String serviceAppName, String clientAppName,
			Long timeout, String user, String password)
			throws Exception {
		super();
		this.serviceInterface = clazz;
		this.serviceAppName = serviceAppName;
		this.target = target;
		this.clientAppName = clientAppName;
		this.timeout = timeout;
		afterPropertiesSet();
	}

	public HedwigClientFactoryBean(Class<?> clazz, String target, String serviceAppName, String clientAppName,
			Long timeout)
			throws Exception {
		super();
		this.serviceInterface = clazz;
		this.serviceAppName = serviceAppName;
		this.target = target;
		this.clientAppName = clientAppName;
		this.timeout = timeout;
		afterPropertiesSet();
	}
	
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
		if (timeout != null && (timeout.longValue() > InternalConstants.DEFAULT_REQUEST_TIMEOUT)) {
			p.setTimeout(timeout);
		}
		if (HedwigUtil.isBlankString(clientAppName)) {
			throw new InvalidParamException("clientAppName must not blank!!!");
		}
		p.setClientAppName(clientAppName);
		if (HedwigUtil.isBlankString(serviceAppName)) {
			throw new InvalidParamException("serviceAppName must not blank!!!");
		}
		p.setServiceAppName(serviceAppName);
		if (HedwigUtil.isBlankString(target)) {
			if (!HedwigUtil.isBlankString(domainName)) {
				p.setDomainName(domainName);
			}
			if (HedwigUtil.isBlankString(serviceName)) {
				throw new InvalidParamException("serviceName must not blank!!!");
			}
			p.setServiceName(serviceName);
			if (HedwigUtil.isBlankString(serviceVersion)) {
				throw new InvalidParamException("serviceVersion must not blank!!!");
			}
			p.setServiceVersion(serviceVersion);
			if (noRetryMethods != null && noRetryMethods.size() > 0) {
				p.setNoRetryMethods(noRetryMethods);
			}
		} else {
			p.setTarget(target);
		}

		return p;
	}

	public void setServiceAppName(String serviceAppName) {
		this.serviceAppName = serviceAppName;
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

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setClientAppName(String clientAppName) {
		this.clientAppName = clientAppName;
	}

	public void setNoRetryMethods(Set<String> noRetryMethods) {
		this.noRetryMethods = noRetryMethods;
	}

}
