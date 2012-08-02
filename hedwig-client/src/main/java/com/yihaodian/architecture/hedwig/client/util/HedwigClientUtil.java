/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.net.MalformedURLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.ProperitesContainer;

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
		String hostIp = ProperitesContainer.getInstance().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			reqId = hostIp + "." + System.nanoTime();
		} finally {
			lock.unlock();
		}
		return reqId;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			long start = HedwigClientUtil.getCurrentNanoTime();
			System.out.println(HedwigClientUtil.generateReqId());
			System.out.println("Cost:" + (HedwigClientUtil.getCurrentNanoTime() - start));
		}
	}

}
