/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;

import com.caucho.hessian.client.HessianProxyFactory;
import com.yihaodian.architecture.hedwig.client.event.BaseEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.engine.DefaultEventEngine;
import com.yihaodian.architecture.hedwig.client.event.handle.SyncRequestHandler;
import com.yihaodian.architecture.hedwig.client.locator.IServiceLocator;
import com.yihaodian.architecture.hedwig.client.locator.ZkServiceLocator;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

/**
 * @author Archer
 * 
 */
public class HedwigEventInterceptor extends RemoteAccessor implements MethodInterceptor, InitializingBean {
	private ClientProfile clientProfile;
	private HessianProxyFactory proxyFactory = new HessianProxyFactory();
	private IServiceLocator<ServiceProfile> locator;
	private Map<String, Object> hessianProxyMap = new ConcurrentHashMap<String, Object>();
	private Class serviceInterface;
	private HedwigContext baseContext;


	@Override
	public Object invoke(MethodInvocation invocation) throws HedwigException {
		Object result = null;
		try {
			HedwigContext eventContext = baseContext.clone();
			BaseEvent syncEvent = null;
			if (HedwigUtil.isBlankString(clientProfile.getTarget())) {
				syncEvent = new BaseEvent(eventContext);
				syncEvent.setInvocation(invocation);
				syncEvent.setHandler(new SyncRequestHandler());
			} else {
				syncEvent = new BaseEvent(eventContext);
				syncEvent.setInvocation(invocation);
				syncEvent.setHandler(new SyncRequestHandler());
			}
			result = DefaultEventEngine.getEngine().syncExecute(syncEvent);
		} catch (Exception e) {
			throw new HedwigException(e.getMessage(), e.getCause());
		}
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		baseContext = new HedwigContext(hessianProxyMap, clientProfile, proxyFactory, serviceInterface);
		try {
			if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
				HedwigClientUtil.createProxy(baseContext, clientProfile.getTarget());
			} else {
				locator = new ZkServiceLocator(clientProfile);
				Collection<ServiceProfile> serviceProfiles = locator.getAllService();
				baseContext.setLocator(locator);
				for (ServiceProfile profile : serviceProfiles) {
					HedwigClientUtil.createProxy(baseContext, profile.getServiceUrl());
				}
			}
		} catch (Exception e) {
			throw new HedwigException(e.getCause());
		}


	}

	/**
	 * Set the interface of the service to access. The interface must be
	 * suitable for the particular service and remoting strategy.
	 * <p>
	 * Typically required to be able to create a suitable service proxy, but can
	 * also be optional if the lookup returns a typed proxy.
	 */
	public void setServiceInterface(Class serviceInterface) {
		if (serviceInterface != null && !serviceInterface.isInterface()) {
			throw new IllegalArgumentException("'serviceInterface' must be an interface");
		}
		this.serviceInterface = serviceInterface;
	}

	/**
	 * Return the interface of the service to access.
	 */
	public Class getServiceInterface() {
		return this.serviceInterface;
	}

	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
}
