/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;

import com.caucho.hessian.client.HessianProxyFactory;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.locator.IServiceLocator;
import com.yihaodian.architecture.hedwig.locator.ZkServiceLocator;

/**
 * @author Archer
 *
 */
public class HedwidEventInterceptor extends RemoteAccessor implements MethodInterceptor, InitializingBean {
	private ClientProfile clientProfile;
	private HessianProxyFactory proxyFactory = new HessianProxyFactory();
	private IServiceLocator<ServiceProfile> locator;
	private Map<String, Object> hessianProxyMap = new HashMap<String, Object>();
	private HedwigContext context;


	@Override
	public Object invoke(MethodInvocation arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
			createProxy(clientProfile.getTarget());
		} else {
			locator = new ZkServiceLocator(clientProfile);
			Collection<ServiceProfile> serviceProfiles = locator.getAllService();
			for (ServiceProfile profile : serviceProfiles) {
				createProxy(profile.getServiceUrl());
			}
		}
		context = new HedwigContext(locator, hessianProxyMap, clientProfile);

	}

	private Object getHessianProxy(String serviceUrl) throws HedwigException {
		Object proxy = null;
		if (hessianProxyMap.containsKey(serviceUrl)) {
			proxy = hessianProxyMap.get(serviceUrl);
		} else {
			proxy = createProxy(serviceUrl);
		}
		return proxy;
	}

	private Object createProxy(String serviceUrl) throws HedwigException {
		Object proxy = null;
		try {
			proxy = proxyFactory.create(getServiceInterface(), serviceUrl);
			if (proxy != null) {
				hessianProxyMap.put(serviceUrl, proxy);
			}
		} catch (MalformedURLException e) {
			throw new HedwigException(e.getMessage());
		}
		return proxy;
	}

	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
}
