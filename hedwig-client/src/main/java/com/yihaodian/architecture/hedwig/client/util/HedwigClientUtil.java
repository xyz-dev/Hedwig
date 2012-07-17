/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.net.MalformedURLException;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;

/**
 * @author Archer
 * 
 */
public class HedwigClientUtil {
	public static Object getHessianProxy(HedwigContext context, String serviceUrl) throws MalformedURLException {
		Object proxy = null;
		if (context.getHessianProxyMap().containsKey(serviceUrl)) {
			proxy = context.getHessianProxyMap().get(serviceUrl);
		} else {
			proxy = createProxy(context, serviceUrl);
		}
		return proxy;
	}

	public static Object createProxy(HedwigContext context, String serviceUrl) throws MalformedURLException {
		Object proxy = null;
		proxy = context.getProxyFactory().create(context.getServiceInterface(), serviceUrl);
		if (proxy != null) {
			context.getHessianProxyMap().put(serviceUrl, proxy);
		}
		return proxy;
	}
}
