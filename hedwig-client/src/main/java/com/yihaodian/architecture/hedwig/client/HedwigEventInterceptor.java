/**
 * 
 */
package com.yihaodian.architecture.hedwig.client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemotingSupport;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.HedwigEventBuilder;
import com.yihaodian.architecture.hedwig.client.event.engine.HedwigEventEngine;
import com.yihaodian.architecture.hedwig.client.hessian.HedwigHessianProxyFactory;
import com.yihaodian.architecture.hedwig.client.locator.IServiceLocator;
import com.yihaodian.architecture.hedwig.client.locator.ZkServiceLocator;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

/**
 * @author Archer
 * 
 */
public class HedwigEventInterceptor extends RemotingSupport implements MethodInterceptor, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(HedwigEventInterceptor.class);
	protected ClientProfile clientProfile;
	private HedwigHessianProxyFactory proxyFactory = new HedwigHessianProxyFactory();
	private IServiceLocator<ServiceProfile> locator;
	private Map<String, Object> hessianProxyMap = new ConcurrentHashMap<String, Object>();
	private Class serviceInterface;
	private HedwigContext eventContext;
	private HedwigEventBuilder eventBuilder;
	private HedwigEventEngine eventEngine;

	@Override
	public Object invoke(MethodInvocation invocation) throws HedwigException {
		Object result = null;
		long start = HedwigClientUtil.getCurrentTime();
		IEvent<Object> event = eventBuilder.buildRequestEvent(invocation);
		try {
			result = eventEngine.syncPoolExec(eventContext, event);
		} catch (Throwable e) {
			logger.error(InternalConstants.LOG_PROFIX + e.getMessage());
		}finally{
			System.out.println("Event execute total time:" + (HedwigClientUtil.getCurrentTime() - start));
		}

		event = null;
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventEngine = HedwigEventEngine.getEngine();
		long timeOut = ProperitesContainer.client().getLongProperty(PropKeyConstants.HEDWIG_READ_TIMEOUT,
				InternalConstants.DEFAULT_READ_TIMEOUT);
		proxyFactory.setReadTimeout(timeOut);
		proxyFactory.setHessian2Request(true);
		proxyFactory.setHessian2Reply(true);
		eventContext = new HedwigContext(hessianProxyMap, clientProfile, proxyFactory, serviceInterface);
		try {
			if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
				HedwigClientUtil.createProxy(eventContext, clientProfile.getTarget());
			} else {
				locator = new ZkServiceLocator(clientProfile);
				Collection<ServiceProfile> serviceProfiles = locator.getAllService();
				eventContext.setLocator(locator);
				for (ServiceProfile profile : serviceProfiles) {
					HedwigClientUtil.createProxy(eventContext, profile.getServiceUrl());
				}
			}
			eventBuilder = new HedwigEventBuilder(eventContext, clientProfile);
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
