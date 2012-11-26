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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemotingSupport;

import com.yihaodian.architecture.hedwig.client.event.BaseEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.HedwigEventBuilder;
import com.yihaodian.architecture.hedwig.client.event.engine.HedwigEventEngine;
import com.yihaodian.architecture.hedwig.client.locator.GroupServiceLocator;
import com.yihaodian.architecture.hedwig.client.locator.IServiceLocator;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.hessian.HedwigHessianProxyFactory;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.monitor.util.MonitorJmsSendUtil;

/**
 * @author Archer
 * 
 */
public class HedwigEventInterceptor extends RemotingSupport implements MethodInterceptor, InitializingBean,
		DisposableBean {
	private Logger logger = LoggerFactory.getLogger(HedwigEventInterceptor.class);
	protected ClientProfile clientProfile;
	private HedwigHessianProxyFactory proxyFactory = new HedwigHessianProxyFactory();
	private IServiceLocator<ServiceProfile> locator;
	private Map<String, Object> hessianProxyMap = new ConcurrentHashMap<String, Object>();
	protected Class serviceInterface;
	private HedwigContext eventContext;
	private HedwigEventBuilder eventBuilder;
	private HedwigEventEngine eventEngine;
	protected String user;
	protected String password;
	protected boolean chunkedPost = true;
	protected boolean overloadedEnable = false;

	@Override
	public Object invoke(MethodInvocation invocation) throws HedwigException {
		Object result = null;
		BaseEvent event = eventBuilder.buildRequestEvent(invocation);
		try {
			result = eventEngine.exec(eventContext, event);
		} catch (HedwigException e) {
			throw e;
		}
		event = null;
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventEngine = HedwigEventEngine.getEngine();
		proxyFactory.setReadTimeout(clientProfile.getTimeout());
		proxyFactory.setHessian2Request(true);
		proxyFactory.setHessian2Reply(true);
		proxyFactory.setChunkedPost(chunkedPost);
		proxyFactory.setOverloadEnabled(overloadedEnable);
		if (!HedwigUtil.isBlankString(user) && !HedwigUtil.isBlankString(password)) {
			proxyFactory.setUser(user);
			proxyFactory.setPassword(password);
		}
		eventContext = new HedwigContext(hessianProxyMap, clientProfile, proxyFactory, serviceInterface);
		try {
			MonitorJmsSendUtil.getInstance();
			if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
				HedwigClientUtil.createProxy(eventContext, clientProfile.getTarget());
			} else {
				locator = new GroupServiceLocator(clientProfile);
				Collection<ServiceProfile> serviceProfiles = locator.getAllService();
				eventContext.setLocator(locator);
				for (ServiceProfile profile : serviceProfiles) {
					HedwigClientUtil.createProxy(eventContext, profile.getServiceUrl());
				}
			}
			eventBuilder = new HedwigEventBuilder(eventContext, clientProfile);
			logger.info("Initial " + clientProfile.getServiceName() + " client successful.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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

	@Override
	public void destroy() throws Exception {
		hessianProxyMap = null;
		if (eventEngine != null) {
			eventEngine.shutdown();
		}
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setChunkedPost(boolean chunkedPost) {
		this.chunkedPost = chunkedPost;
	}

	public void setOverloadedEnable(boolean overloadedEnable) {
		this.overloadedEnable = overloadedEnable;
	}

}
