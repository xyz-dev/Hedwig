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
import com.yihaodian.architecture.hedwig.common.uuid.UUID;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

/**
 * @author Archer
 * 
 */
public class HedwigClientUtil {

	private static Lock lock = new ReentrantLock();
	private static String shortIP;
	static {
		genShortIp();
	}

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

	public static String generateReqId(IEvent<Object> event) {
		String reqId = "";
		reqId = "req-" + HedwigUtil.getCurrentTime() + "-" + shortIP + event.hashCode();
		return reqId;
	}

	public static String generateGlobalId(IEvent<Object> event) {
		String glbId = "";
		glbId = "glb-" + HedwigUtil.getCurrentTime() + "-" + shortIP + event.hashCode();
		return glbId;
	}

	public static String generateTransactionId() {
		String txnId = "";
		txnId = "txn-" + new UUID().toString();
		return txnId;
	}

	public static int getRedoCount(HedwigContext context) {
		int nodeCount = context.getLocator().getAllService().size();
		int redoCount = nodeCount >= 1 ? (nodeCount - 1) : 0;
		return redoCount;
	}

	public static void genShortIp() {
		StringBuilder sb = new StringBuilder();
		String hostIp = ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP, "");
		if (!HedwigUtil.isBlankString(hostIp)) {
			String[] nodes = hostIp.split("\\.");
			if (nodes != null && nodes.length == 4) {
				sb.append(nodes[2]).append(".").append(nodes[3]).append("-");
			}
		}
		shortIP = sb.toString();
	}

	public static String generateReqId(IEvent<Object> event, String clientAppName, String serviceAppName) {
		String reqId = "";
		reqId = clientAppName + "-" + serviceAppName + "-" + HedwigUtil.getCurrentTime() + "-" + shortIP
				+ event.hashCode();
		return reqId;
	}

}
