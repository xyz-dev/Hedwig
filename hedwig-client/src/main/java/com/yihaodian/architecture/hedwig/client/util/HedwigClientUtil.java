/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.net.MalformedURLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;

/**
 * @author Archer
 * 
 */
public class HedwigClientUtil {

	private static Lock lock = new ReentrantLock();

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

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static long getCurrentNanoTime() {
		return System.nanoTime();
	}

	public static String generateReqId() {
		String reqId = "";
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			reqId = hostIp + "." + getCurrentNanoTime();
		} finally {
			lock.unlock();
		}
		return reqId;
	}

	public static String generateGlobalId() {
		String reqId = "";
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			reqId = "global." + hostIp + "." + getCurrentNanoTime();
		} finally {
			lock.unlock();
		}
		return reqId;
	}
}
