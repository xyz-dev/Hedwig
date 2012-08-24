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
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

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


	public static String generateReqId() {
		String reqId = "";
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			reqId = "req." + hostIp + "." + HedwigUtil.getCurrentNanoTime();
		} finally {
			lock.unlock();
		}
		return reqId;
	}

	public static String generateGlobalId() {
		String glbId = "";
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			glbId = "glb." + hostIp + "." + HedwigUtil.getCurrentNanoTime();
		} finally {
			lock.unlock();
		}
		return glbId;
	}

	public static String generateTransactionId() {
		String txnId = "";
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP);
		lock.lock();
		try {
			txnId = "txn." + hostIp + "." + HedwigUtil.getCurrentNanoTime();
		} finally {
			lock.unlock();
		}
		return txnId;
	}
	public static int getRedoCount(HedwigContext context) {
		int nodeCount = context.getLocator().getAllService().size();
		int redoCount = nodeCount >= 1 ? (nodeCount - 1) : 0;
		return redoCount;
	}
}
