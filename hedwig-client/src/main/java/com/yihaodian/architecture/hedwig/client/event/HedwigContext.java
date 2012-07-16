/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.Map;

import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.engine.event.EventContext;
import com.yihaodian.architecture.hedwig.locator.IServiceLocator;

/**
 * @author Archer
 *
 */
public class HedwigContext implements EventContext{
	private IServiceLocator<ServiceProfile> locator;
	private Map<String, Object> hessianProxyMap;
	private ClientProfile clientProfile;

	public IServiceLocator<ServiceProfile> getLocator() {
		return locator;
	}

	public void setLocator(IServiceLocator<ServiceProfile> locator) {
		this.locator = locator;
	}

	public Map<String, Object> getHessianProxyMap() {
		return hessianProxyMap;
	}

	public void setHessianProxyMap(Map<String, Object> hessianProxyMap) {
		this.hessianProxyMap = hessianProxyMap;
	}

	public HedwigContext() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HedwigContext(IServiceLocator<ServiceProfile> locator, Map<String, Object> hessianProxyMap, ClientProfile clientProfile) {
		super();
		this.locator = locator;
		this.hessianProxyMap = hessianProxyMap;
		this.clientProfile = clientProfile;
	}

}
