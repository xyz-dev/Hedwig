/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Archer
 * 
 */
public class HedwigClientFactoryBean extends HedwigEventInterceptor implements FactoryBean {

	private Object serviceProxy;

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
		super.afterPropertiesSet();
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
	}
}
